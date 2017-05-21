/**
 * App - runs the application.
 */
package org.jfeild1337.credsmgr.app;

import java.awt.EventQueue;

public class App {

    // RUN IT
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CredentialsManagerMain window = new CredentialsManagerMain();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
