package entity;
import javax.swing.*;
import java.awt.*;

public class Window {
    //I stole this, not the primary objective at the moment.
    public Window(int w, int h, String title, Simulation sim){
        sim.setPreferredSize(new Dimension(w, h));
        sim.setMaximumSize(new Dimension(w, h));
        sim.setMinimumSize(new Dimension(w, h));

        JFrame frame = new JFrame(title);
        frame.add(sim);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        sim.start();

    }
}
