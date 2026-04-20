import javax.swing.*;

public class MarioGame extends JFrame{
  public MarioGame(){
    setTitle("Mario Game");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

  GamePanel gamePanel = new GamePanel();
    add(gamePanel);

  pack();
    setLocationRelativeTo(null);
  }
  
  public static void main (String []args ){
SwingUtilities.invokeLater(() -> {
            new MarioGame().setVisible(true);}
                           }
}
