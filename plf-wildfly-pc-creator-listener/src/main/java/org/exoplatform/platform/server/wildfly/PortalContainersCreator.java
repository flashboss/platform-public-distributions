/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.server.wildfly;

import static org.exoplatform.commons.utils.SecurityHelper.doPrivilegedAction;
import static org.exoplatform.container.RootContainer.getInstance;

import java.security.PrivilegedAction;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.exoplatform.container.RootContainer;

/**
 *
 */
public class PortalContainersCreator implements ServletContextListener {

	/**
	 * Initializes and creates all the portal container that have been registered
	 * previously
	 */
	public void createPortalContainers() {
		final RootContainer rootContainer = getInstance();
		doPrivilegedAction(new PrivilegedAction<Void>() {
			public Void run() {
				rootContainer.createPortalContainers();
				return null;
			}
		});
	}

	/**
	 * Ensure that the root container is stopped properly since the shutdown hook
	 * doesn't work in some cases for example with tomcat when we call the stop
	 * command
	 */
	public void releasePortalContainers() {
		doPrivilegedAction(new PrivilegedAction<Void>() {
			public Void run() {
				getInstance().stop();
				return null;
			}
		});
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		createPortalContainers();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		releasePortalContainers();
	}

}
