package com.design.life;

import acm.graphics.GObject;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class Life extends WindowProgram {

    public static final int APPLICATION_WIDTH = 1000;
    public static final int APPLICATION_HEIGHT = 600;

    private static final int BOX_SIZE = 10;
    public static final Color BOX_COLOR = Color.RED;
    private static final int WORLD_WIDTH = APPLICATION_WIDTH / BOX_SIZE;
    private static final int WORLD_HEIGHT = (APPLICATION_HEIGHT - 70) / BOX_SIZE;
    public static final Color WORLD_COLOR = Color.GRAY;
    private JButton startButton;
    private JButton deathButton;
    private JButton populateButton;
    private JButton populatePieceButton;
    private JButton gliderButton;
    private boolean lifeGoOn = true;
    private boolean piece = false;
    private final RandomGenerator rgen = RandomGenerator.getInstance();

    public void mouseClicked(MouseEvent me) {
        int x = me.getX();
        int y = me.getY();
        if (!piece) {
            GObject newBox = getElementAt(x, y);
            if (newBox != null)
                remove(newBox);
            else
                drawBox(x - x % BOX_SIZE, y - y % BOX_SIZE);
        } else {
            populateRandomlyPiece(x, y);
        }
    }

    public void init() {
        getMenuBar().setVisible(false);
        setBackground(WORLD_COLOR);
        addMouseListeners();

        startButton = new JButton("START/STOP");
        add(startButton, NORTH);

        deathButton = new JButton("DESTROY THIS WORLD");
        add(deathButton, NORTH);

        populateButton = new JButton("POPULATE THE WORLD");
        add(populateButton, SOUTH);

        populatePieceButton = new JButton("POPULATE THE PIECE");
        add(populatePieceButton, SOUTH);

        gliderButton = new JButton("CREATE GLIDER");
        add(gliderButton, SOUTH);

        addActionListeners();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            lifeGoOn = !lifeGoOn;
        }
        if (e.getSource() == deathButton) {
            lifeGoOn = false;
            removeAll();
        }
        if (e.getSource() == populateButton) {
            populateRandomlyWorld();
        }
        if (e.getSource() == populatePieceButton) {
            piece = !piece;
        }
        if (e.getSource() == gliderButton) {
            drawGlider();
        }
    }

    private void populateRandomlyPiece(int x, int y) {
        int m = (x - x % BOX_SIZE) / BOX_SIZE;
        int n = (y - y % BOX_SIZE) / BOX_SIZE;
        for (int j = n - 5; j <= n + 5; j++) {
            for (int i = m - 5; i <= m + 5; i++) {
                if (i >= 0 && i < WORLD_WIDTH && j >= 0 && j < WORLD_HEIGHT)
                    if (rgen.nextBoolean(0.2))
                        drawBox(i * BOX_SIZE, j * BOX_SIZE);
            }
        }
    }

    private void populateRandomlyWorld() {
        for (int j = 0; j < WORLD_HEIGHT; j++) {
            for (int i = 0; i < WORLD_WIDTH; i++) {
                if (rgen.nextBoolean(0.2))
                    drawBox(i * BOX_SIZE, j * BOX_SIZE);
            }
        }
    }

    public void run() {

        int[][] present = new int[WORLD_WIDTH][WORLD_HEIGHT];
        int[][] future = new int[WORLD_WIDTH][WORLD_HEIGHT];

        while (true) {
            pause(1);
            while (lifeGoOn) {
                pause(50);
                createPresent(present);
                createFuture(present, future);
                drawFuture(future);
            }
        }
    }

    private void drawFuture(int[][] future) {
        for (int j = 0; j < WORLD_HEIGHT; j++) {
            for (int i = 0; i < WORLD_WIDTH; i++) {
                GObject box = getElementAt(i * BOX_SIZE, j * BOX_SIZE);
                if (future[i][j] == 0 && box != null) {
                    remove(box);
                } else if (future[i][j] == 1 && box == null) {
                    drawBox(i * BOX_SIZE, j * BOX_SIZE);
                }
            }
        }
    }

    private void createFuture(int[][] present, int[][] future) {
        for (int j = 0; j < WORLD_HEIGHT; j++) {
            for (int i = 0; i < WORLD_WIDTH; i++) {
                if (present[i][j] == 0 && sum(i, j, present) == 3)
                    future[i][j] = 1;
                else if (present[i][j] == 1 && (sum(i, j, present) < 3 || sum(i, j, present) > 4))
                    future[i][j] = 0;
                else future[i][j] = present[i][j];
            }
        }
    }

    private void createPresent(int[][] present) {
        for (int j = 0; j < WORLD_HEIGHT; j++) {
            for (int i = 0; i < WORLD_WIDTH; i++) {
                GObject box = getElementAt(i * BOX_SIZE, j * BOX_SIZE);
                if (box == null)
                    present[i][j] = 0;
                else present[i][j] = 1;
            }
        }
    }

    private void drawGlider() {
        int x = 0;
        int y = 0;
        drawBox(x * BOX_SIZE, y * BOX_SIZE);
        drawBox((x + 1) * BOX_SIZE, (y + 1) * BOX_SIZE);
        drawBox((x + 2) * BOX_SIZE, (y + 1) * BOX_SIZE);
        drawBox((x + 1) * BOX_SIZE, (y + 2) * BOX_SIZE);
        drawBox(x * BOX_SIZE, (y + 2) * BOX_SIZE);
    }

    private int sum(int x, int y, int[][] present) {
        int s = 0;
        for (int j = y - 1; j <= y + 1; j++) {
            for (int i = x - 1; i <= x + 1; i++) {
                // in the field
                if (i >= 0 && i < WORLD_WIDTH && j >= 0 && j < WORLD_HEIGHT) {
                    s = s + present[i][j];
                }
                // in edge of the field
                if (j < 0 && (i >= 0 && i < WORLD_WIDTH)) s = s + present[i][WORLD_HEIGHT - 1];
                if (j >= WORLD_HEIGHT && (i >= 0 && i < WORLD_WIDTH)) s = s + present[i][0];
                if (i < 0 && (j >= 0 && j < WORLD_HEIGHT)) s = s + present[WORLD_WIDTH - 1][j];
                if (i >= WORLD_WIDTH && (j >= 0 && j < WORLD_HEIGHT)) s = s + present[0][j];
            }
        }
        return s;
    }

    private void drawBox(double x, double y) {
        GObject box = getElementAt(x, y);
        if (box == null) {
            GRect rectangle = new GRect(x, y, BOX_SIZE - 2, BOX_SIZE - 2);
            rectangle.setColor(BOX_COLOR);
            rectangle.setFilled(true);
            add(rectangle);
        }
    }
}