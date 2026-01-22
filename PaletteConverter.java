import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PaletteConverter {

    private List<int[]> PALETTE_COLORS = new ArrayList<>();

    public PaletteConverter(String palettePath) throws IOException {
        loadPalette(palettePath);
    }

    private void loadPalette(String palettePath) throws IOException {
        BufferedImage palette = ImageIO.read(new File(palettePath));
        int width = palette.getWidth();
        int height = palette.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = palette.getRGB(x, y);
                int[] colors = {(rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF};
                PALETTE_COLORS.add(colors);
            }
        }
    }

    public void convertImageColorsToPaletteAndSave(String filePath) throws IOException {
        System.out.println("Input file: " + filePath);
        long start = System.currentTimeMillis();
        BufferedImage image = ImageIO.read(new File("input/" + filePath));
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int[] nearestRgb = getNearestColorInPalette(rgb);
                resultImage.setRGB(x, y, (nearestRgb[0] << 16) | (nearestRgb[1] << 8) | nearestRgb[2]);
            }
        }

        ImageIO.write(resultImage, "png", new File("output/" + filePath));

        long finish = System.currentTimeMillis();
        System.out.println("time to render: " + (finish - start) + " ms");
    }

    private int[] getNearestColorInPalette(int rgb) {
        double minLength = Double.MAX_VALUE;
        int[] nearestColor = {0, 0, 0};
        for (int[] color : PALETTE_COLORS) {
            double length = Math.sqrt(
                    Math.pow((color[0] - ((rgb >> 16) & 0xFF)), 2) +
                    Math.pow((color[1] - ((rgb >> 8) & 0xFF)), 2) +
                    Math.pow((color[2] - (rgb & 0xFF)), 2)
            );
            if (length < minLength) {
                minLength = length;
                nearestColor = color;
            }
        }
        return nearestColor;
    }

    public static void main(String[] args) throws IOException {
        //carrega palette
        String palettePath = args[0];
        System.out.println("palettePath: " + palettePath);
        PaletteConverter converter = new PaletteConverter(palettePath);
        //cria os diretorios se não existirem
        Path inputFolderPath = Paths.get("input/");
        Path outputFolderPath = Paths.get("output/");
        if (!Files.exists(inputFolderPath) || !Files.exists(outputFolderPath)) {
            try {
                Files.createDirectories(inputFolderPath);
            } catch (IOException ignore) { }
            try {
                Files.createDirectories(outputFolderPath);
            } catch (IOException ignore) { }
        }
        
        //converte todas as imagens de input se ela ainda não forem convertidas
        File[] inputFiles = new File("input").listFiles();
        File outputFolder = new File("output");
        for (File file : inputFiles) {
            String filePath = file.getName();
            if (!new File(outputFolder, filePath).exists()) {
                converter.convertImageColorsToPaletteAndSave(filePath);
            }
        }
    }
    
    
}
