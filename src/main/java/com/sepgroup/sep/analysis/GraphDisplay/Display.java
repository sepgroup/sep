package com.sepgroup.sep.analysis.GraphDisplay;

/**
 * Created by HP on 8/4/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.event.KeyListener;

import javax.swing.*;

public class Display extends JFrame {
    ArrayList<Drawable> renderObjects = new ArrayList<>(5);
    KeyInputController keyController;
    JPanel masterPanel;
    JLabel coulombs;
    JLabel hooks;
    static Color background = Color.WHITE;
    public Display()
    {
        super("Test");
        setSize(1200,800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        masterPanel = new GPanel();
        coulombs = new JLabel("COULOMBS: "+ PhysicsNode.coulombsConstant+"\t");
        hooks = new JLabel("HOOKS: "+ PhysicsNode.hooksConstant+"\t");
        masterPanel.add(coulombs);
        masterPanel.add(hooks);
        add(masterPanel);
        setVisible(true);

    }

    public void addRenderObject(Drawable d){
        if(!renderObjects.contains(d))
            renderObjects.add(d);

    }
    class GPanel extends JPanel implements KeyListener {
        public GPanel(){
            super();
            super.setBackground(background);
            this.addKeyListener(this);
            setFocusable(true);
        }
        public void paintComponent(Graphics g1){
            Graphics2D g = (Graphics2D) g1;
            super.paintComponent(g);
            for(Drawable d : renderObjects)
                d.draw(g);
        }
        @Override
        public void keyPressed(KeyEvent e) {
            keyController.pressEvent(e);

        }
        @Override
        public void keyReleased(KeyEvent e) {
            keyController.releaseEvent(e);
        }
        @Override
        public void keyTyped(KeyEvent e) {

        }


    }
}
