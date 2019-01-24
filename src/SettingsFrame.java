import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Pwnxl on 2017-04-28.
 *
 * Allows the user to input settings
 */
public class SettingsFrame extends JFrame{

    JLabel width_label, height_label, size_label;
    JPanel left, right;
    JTextField width_text, height_text, size_text;
    JButton start;
    boolean done;
    int width, height, size;

    public SettingsFrame ()
    {
        done = false;
        setLayout(new BorderLayout());
        setBounds(100,100,0,0);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Game of Life");
        setResizable(false);

        setup_panels();
        setup_labels();
        setup_textfields();
        setup_button();

        pack();
        setVisible(true);
    }

    private void setup_panels ()
    {
        left = new JPanel();
        right = new JPanel();
        left.setLayout(new BorderLayout());
        right.setLayout(new BorderLayout());
        add(left,BorderLayout.WEST);
        add(right,BorderLayout.CENTER);
    }

    private void setup_labels ()
    {
        width_label = new JLabel(" Width");
        height_label = new JLabel(" Height");
        size_label = new JLabel(" Tile Size ");
        left.add(width_label,BorderLayout.NORTH);
        left.add(height_label,BorderLayout.CENTER);
        left.add(size_label,BorderLayout.SOUTH);
    }

    private void setup_textfields ()
    {
        width_text = new JTextField("45");
        height_text = new JTextField("45");
        size_text = new JTextField("15");
        width_text.setPreferredSize(new Dimension(120,20));
        height_text.setPreferredSize(new Dimension(120,20));
        size_text.setPreferredSize(new Dimension(120,20));
        right.add(width_text,BorderLayout.NORTH);
        right.add(height_text,BorderLayout.CENTER);
        right.add(size_text,BorderLayout.SOUTH);
    }

    private void setup_button ()
    {
        start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean flag = true;
                try
                {
                    width = Integer.parseInt(width_text.getText());
                    if (width < 1) throw new NumberFormatException();
                }catch (NumberFormatException ex)
                {
                    width_text.setText("error");
                    flag = false;
                }
                try
                {
                    height = Integer.parseInt(height_text.getText());
                    if (height < 1) throw new NumberFormatException();
                }catch (NumberFormatException ex)
                {
                    height_text.setText("error");
                    flag = false;
                }
                try
                {
                    size = Integer.parseInt(size_text.getText());
                    if (size < 1) throw new NumberFormatException();
                }catch (NumberFormatException ex)
                {
                    size_text.setText("error");
                    flag = false;
                }
                if (flag)
                {
                    done = true;
                }
            }
        });
        add(start,BorderLayout.EAST);
    }

    public boolean isDone ()
    {
        return done;
    }

    public Setting get_setting ()
    {
        return new Setting(width, height, size);
    }
}
