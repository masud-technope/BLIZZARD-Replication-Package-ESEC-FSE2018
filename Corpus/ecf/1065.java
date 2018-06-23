/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.githubservice;

import java.util.concurrent.Future;

/**
 * @since 1.0
 */
public interface IGitHubServiceAsync {

    /**
	 * Gets all repositories.
	 * 
	 * @param pAccessToken
	 *            see https://help.github.com/articles/creating-an-access-token-
	 *            for-command-line-use/
	 * @return All this repositories for this access token
	 */
    public Future<String[]> getRepositoriesAsync(String pAccessToken);
}
