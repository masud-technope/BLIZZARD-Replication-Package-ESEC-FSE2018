/*******************************************************************************
 * Copyright (c) 2014 Remain BV All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Wim Jongman - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.githubservice.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import com.mycorp.examples.githubservice.IGitHubServiceAsync;

/**
 * Example OSGi service for retrieving some data from GitHub.
 * 
 * @since 1.0
 */
public class GitHubServiceAsync implements IGitHubServiceAsync {

    /**
	 * Stand alone service test. To get an access token see see
	 * https://help.github.com/articles/creating-an-access-token-
	 * for-command-line-use/
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String token = "your token here";
        GitHubServiceAsync service = new GitHubServiceAsync();
        Future<String[]> future = service.getRepositoriesAsync(token);
        System.out.print("Z");
        while (!future.isDone()) {
            Thread.sleep(200);
            System.out.print("z");
        }
        System.out.println();
        System.out.println();
        for (String repository : future.get()) {
            System.out.println(repository);
        }
    }

    public Future<String[]> getRepositoriesAsync(final String pAccessToken) {
        FutureTask<String[]> future = new FutureTask<String[]>(new Callable<String[]>() {

            public String[] call() throws Exception {
                Thread.sleep(5000);
                GitHubService service = new GitHubService();
                return service.getRepositories(pAccessToken);
            }
        });
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.execute(future);
        executor.shutdown();
        return future;
    }
}
