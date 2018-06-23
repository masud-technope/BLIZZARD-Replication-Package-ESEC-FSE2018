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

import java.io.IOException;
import java.util.List;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import com.mycorp.examples.githubservice.IGitHubService;

/**
 * Example OSGi service for retrieving some data from GitHub.
 * 
 * @since 1.0
 */
public class GitHubService implements IGitHubService {

    /**
	 * Stand alone service test. To get an access token see see
	 * https://help.github.com/articles/creating-an-access-token-
	 * for-command-line-use/
	 */
    public static void main(String[] args) {
        String token = "your token here";
        GitHubService service = new GitHubService();
        String[] repos = service.getRepositories(token);
        for (String repository : repos) {
            System.out.println(repository);
            System.out.println("================");
            String[] issues = service.getIssues(token, "wimjongman", repository);
            if (issues.length == 0) {
                System.out.println("*** No Issues ***");
            }
            for (String issue : issues) {
                System.out.println(issue);
            }
            System.out.println();
        }
    }

    public String[] getRepositories(String pAccessToken) {
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(pAccessToken);
        RepositoryService service = new RepositoryService(client);
        try {
            List<Repository> repositories = service.getRepositories();
            String[] result = new String[repositories.size()];
            for (int i = 0; i < repositories.size(); i++) {
                result[i] = repositories.get(i).getName();
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    public String[] getIssues(String pAccessToken, String owner, String pRepository) {
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(pAccessToken);
        IssueService issueService = new IssueService(client);
        try {
            List<Issue> issues = issueService.getIssues("wimjongman", pRepository, null);
            String[] result = new String[issues.size()];
            for (int i = 0; i < issues.size(); i++) {
                result[i] = issues.get(i).getNumber() + "::" + issues.get(i).getTitle();
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }
}
