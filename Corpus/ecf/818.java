/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.bulletinboard;

/**
 * Models a poll vote. The vote may either be a summary or a particular vote,
 * containing a reference to the member who voted.
 * 
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will work or
 * that it will remain the same. Please do not use this API without consulting
 * with the ECF team.
 * </p>
 * 
 * @author Erkki
 */
public interface IPollVote {

    /**
	 * This is a summary vote, containing the number of votes this option got.
	 */
    public static final int SUMMARY_VOTE = 1;

    /**
	 * This is a detail vote, having 1 as the number of votes and an optional
	 * reference to the member who voted.
	 */
    public static final int DETAIL_VOTE = 2;

    /**
	 * Returns the type of this vote. SUMMARY_VOTE or DETAIL_VOTE.
	 * 
	 * @return the type of this vote
	 */
    public int getType();

    /**
	 * The poll that this vote belongs to.
	 * 
	 * @return the poll
	 */
    public IPoll getPoll();

    /**
	 * The option that was voted for.
	 * 
	 * @return the option
	 */
    public IPollOption getOption();

    /**
	 * The member who voted. We are assuming that only Members can vote, and
	 * anonymous users are not allowed to vote.
	 * 
	 * @return the voter
	 */
    public IMember getVoter();

    /**
	 * The number of votes. This may be 1, when this is a detail vote, and only
	 * members can vote; n, when this is a summary vote.
	 * 
	 * @return the number of votes
	 */
    public int getQuantity();
}
