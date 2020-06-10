package lol.maki.lab;

import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class SyslogServer implements DisposableBean {
	private final SyslogServerProps props;

	private final DisposableServer server;

	public SyslogServer(SyslogServerProps props, SyslogHandler syslogHandler) {
		this.props = props;
		this.server = TcpServer.create()
				.host(props.host())
				.port(props.port())
				.wiretap(true)
				.handle(syslogHandler)
				.bindNow();
	}

	@Override
	public void destroy() {
		this.server.onDispose().subscribe();
	}
}
