package com.example.apkpencatatankeuangan.controller;

import java.io.*;
import java.time.LocalDate;
import java.util.Date;

public class SessionManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SESSION_FILE = "session.ser";
    private static volatile SessionManager instance;
    private boolean isLoggedIn;
    private LocalDate date;
    private String username, password;

    // Private constructor to prevent instantiation from outside
    private SessionManager() {
        isLoggedIn = false;
    }

    // Static method to get the singleton instance
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                    instance.createSessionFile();
                }
            }
        }
        return instance;
    }

    private void createSessionFile() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            saveSession();
        } else {
            loadSession();
        }

    }

    private void loadSession() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SESSION_FILE))) {
            SessionManager sessionManager = (SessionManager) ois.readObject();
            this.isLoggedIn = sessionManager.isLoggedIn;
            this.date = sessionManager.date;
            this.username = sessionManager.username;
            this.password = sessionManager.password;

            if (date == null) {
                // Jika tanggal null, anggap session expired
                isLoggedIn = false;
            } else {
                // Cek apakah session sudah lebih dari 3 hari atau tanggal session di masa depan
                int diff = LocalDate.now().getDayOfYear() - date.getDayOfYear();
                if (diff > 3 || diff < 0) {
                    isLoggedIn = false;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading session: " + e.getMessage());
            // Jika gagal load session, set default
            this.isLoggedIn = false;
            this.date = null;
            this.username = null;
            this.password = null;
        }
    }


    private void saveSession() {
        System.out.println("Saving session...");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(this);
            System.out.println("Session saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }



}
    public String getUsername() {
        return username;
    }

    // Method to check if user is logged in
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    // Method to simulate login
    public void login(String username, String password) {
        this.date = LocalDate.now();
        this.isLoggedIn = true;
        this.username = username;
        this.password = password;
        saveSession();
    }

    // Method to simulate logout
    public void logout() {
        this.isLoggedIn = false;
        this.username = null;
        this.password = null;
        this.date = null;
        saveSession();
    }


}