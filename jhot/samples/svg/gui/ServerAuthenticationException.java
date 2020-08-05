/*
 * @(#)ServerAuthenticationException.java  1.0  2007-05-29
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.samples.svg.gui;

import java.io.*;

/**
 * Thrown to indicate that a request failed because we didn't authenticate
 * successfully.
 * <p>
 * This exception is used for communication with PmWiki. PmWiki does not 
 * return an HTTP status code, when the user failed to authenticate, instead
 * it opens a page with a password form.
 *
 * @author Werner Randelshofer
 * @version 1.0 2007-05-29 Created.
 */
public class ServerAuthenticationException extends IOException {
    
    /**
     * Creates a new instance of <code>ServerAuthenticationException</code> without detail message.
     */
    public ServerAuthenticationException() {
    }
    
    
    /**
     * Constructs an instance of <code>ServerAuthenticationException</code> with the specified detail message.
     * 
     * @param msg the detail message.
     */
    public ServerAuthenticationException(String msg) {
        super(msg);
    }
}
