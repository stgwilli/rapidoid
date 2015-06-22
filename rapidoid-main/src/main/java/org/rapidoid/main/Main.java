package org.rapidoid.main;

/*
 * #%L
 * rapidoid-main
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Method;
import java.util.List;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.scan.Scan;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.1.0")
public class Main {

	public static void main(String[] args) {
		processHelp(args);

		List<Class<?>> app = Scan.annotated(App.class);
		if (!app.isEmpty()) {
			Class<?> appCls = app.get(0);

			Method main = Cls.getMethod(appCls, "main", String[].class);
			if (main != null) {
				Object[] mainArgs = new Object[] { args };
				Cls.invokeStatic(main, mainArgs);
			}
		}
	}

	public static void processHelp(Object[] args) {
		if (args.length == 1 && args[0].equals("--help")) {
			System.out.println("Usage:");
			System.out.println("  java [-cp <classpath>] -jar <app>.jar [option1 option2 ...] ");

			System.out.println("\nExample:");
			System.out.println("  java -jar myapp.jar port=9090 address=127.0.0.1 cpus=2 workers=4");

			System.out.println("\nAvailable options:");
			opt("mode=(dev|production)", "configure DEV or PRODUCTION mode");
			opt("port=<P>", "listen at port P (default: 8080)");
			opt("address=<ADDR>", "listen at address ADDR (default: 0.0.0.0)");
			opt("cpus=<C>", "optimize for C number of CPUs (default: the actual number of the CPUs)");
			opt("workers=<W>", "start W number of I/O workers (default: the configured number of CPUs)");
			opt("nodelay", "set the TCP_NODELAY flag to disable Nagle's algorithm (default: false)");
			opt("blockingAccept", "accept connection in BLOCKING mode (default: false)");
			opt("bufSizeKB=<SIZE>", "TCP socket buffer size in KB (default: 16)");

			System.exit(0);
		}
	}

	private static void opt(String opt, String desc) {
		System.out.println("  " + opt + U.copyNtimes(" ", 17 - opt.length()) + " - " + desc);
	}

}