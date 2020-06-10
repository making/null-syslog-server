package lol.maki.lab;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "syslog.server")
@ConstructorBinding
public class SyslogServerProps {
	private final String host;

	private final int port;

	public SyslogServerProps(String host, int port) {
		this.host = host;
		this.port = port;
	}


	public String host() {
		return host;
	}

	public int port() {
		return port;
	}
}
