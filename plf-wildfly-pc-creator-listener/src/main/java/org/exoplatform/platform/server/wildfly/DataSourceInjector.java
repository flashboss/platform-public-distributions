/*
 * Copyright (C) 2016 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
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

import static org.exoplatform.container.ExoContainerContext.getCurrentContainer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.jboss.system.Service;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com
 */
public class DataSourceInjector implements Service {
	private static final Log LOG = ExoLogger.getLogger(DataSourceInjector.class);

	private Context getGlobalNamingContext() throws Exception {
		Context context = new InitialContext();
		return (Context) context.lookup("java:global");
	}

	private NamingEnumeration<NameClassPair> getListOfDatasources(Context globalNamingContext) throws NamingException {
		NamingEnumeration<NameClassPair> list = globalNamingContext.list("/");
		return list;
	}

	@Override
	public void create() throws Exception {
	}

	@Override
	public void start() throws Exception {
		PortalContainer portalContainer = (PortalContainer) getCurrentContainer();
		String portalName = portalContainer.getName();

		if (LOG.isDebugEnabled()) {
			LOG.debug("register portal container classLoader inside " + portalName);
		}

		Context globalNamingContext = getGlobalNamingContext();
		if (globalNamingContext == null) {
			throw new IllegalStateException("Can't access Tomcat Global context");
		}

		NamingEnumeration<NameClassPair> datasources = getListOfDatasources(globalNamingContext);

		// Inject PortalContainer class loader as authorized to access tomcat
		// global
		// datasources
		while (datasources.hasMoreElements()) {
			NameClassPair datasource = datasources.next();
			globalNamingContext.bind("java:comp/env/" + datasource.getName(), datasource);
		}
	}

	@Override
	public void stop() {
	}

	@Override
	public void destroy() {
	}
}
