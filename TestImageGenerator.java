import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class TestImageGenerator {
    public static void main(String[] args) throws Exception {
        int width = 800;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 36));
        
        g2d.drawString("Government of India", 50, 50);
        g2d.drawString("Aadhaar Card", 50, 100);
        g2d.drawString("Name: John Doe", 50, 150);
        g2d.drawString("DOB: 01/01/1990", 50, 200);
        g2d.drawString("1234 5678 9012", 50, 250);
        g2d.drawString("john.doe@example.com", 50, 300);
        
        g2d.dispose();
        
        ImageIO.write(image, "png", new File("aadhaar_test.png"));
        System.out.println("Image generated.");
    }
}
