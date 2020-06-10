package lol.maki.lab;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

import org.springframework.stereotype.Component;

@Component
public class SyslogHandler
		implements BiFunction<NettyInbound, NettyOutbound, Publisher<Void>> {
	private final Counter ingressPayloads;

	private final Counter ingressLogs;

	private final Counter backpressureDroppedLogs;

	private static final Logger log = LoggerFactory.getLogger(SyslogHandler.class);

	private static final String LF = "\n";

	private static final String SPLIT_PATTERN = "(?<=" + LF + ")";


	public SyslogHandler(MeterRegistry meterRegistry) {
		this.ingressPayloads = meterRegistry.counter("payloads.ingress");
		this.ingressLogs = meterRegistry.counter("logs.ingress");
		this.backpressureDroppedLogs = meterRegistry.counter("logs.backpressure.dropped");
	}

	@Override
	public Publisher<Void> apply(NettyInbound in, NettyOutbound out) {
		Flux<String> ingress = in.receive().asString();
		ingress.doOnNext(__ -> this.ingressPayloads.increment())
				.transform(this::parse)
				.doOnNext(__ -> this.ingressLogs.increment())
				.subscribe();
		return Flux.never();
	}

	Flux<String> parse(Flux<String> ingress) {
		return ingress
				.flatMapIterable(s -> Arrays.asList(s.split(SPLIT_PATTERN)))
				.windowUntil(s -> s.endsWith(LF))
				.flatMap(f -> f.collect(Collectors.joining()))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.onBackpressureDrop(this::onDropped)
				.doOnNext(log::debug);
	}


	void onDropped(String s) {
		this.backpressureDroppedLogs.increment();
		log.warn("Dropped! {}", s);
	}
}
