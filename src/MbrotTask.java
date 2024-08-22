import org.apache.commons.math3.complex.Complex;

public class MbrotTask {

    public final int subtaskNumber;
    public int[] rgbArray;
    public final int startX;
    public final int startY;
    public final int width;
    public final int height;
    public final MbrotContext context;

    public MbrotTask(int subtaskNumber, int startX, int startY, int width, int height, MbrotContext context) {
        this.subtaskNumber = subtaskNumber;
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.context = context;
        this.rgbArray = new int[this.width * this.height];
    }

    public void execute() {
        Complex c;
        Complex zi;
        int iteration = 0;

        for (int x = 0; x < this.width; x++) {

            for (int y = 0; y < this.height; y++) {
                double a = context.aStart + ((this.startX + x) * context.pointOffsetA);
                double b = context.bStart + ((this.startY + y) * context.pointOffsetB);
                c = new Complex(a, b);
                zi = c; // i = 0, Z(0) = C

                iteration = 0;
                while (zi.abs() <= Constants.ESCAPE_RADIUS && iteration < context.maxIterations) {
                    zi = zi.multiply(zi).add(c);
                    iteration++;
                };
                this.rgbArray[(y * this.width) + x] = getColor(iteration, context.maxIterations);
            }
        }
    }

    /**
     * This coloring functions is taken from Eric Mosher's Mandelbrot implementation
     * https://github.com/emosher/Mandelbrot/tree/master
     */

    public static int getColor(int i, int maxIterations) {
        // A color scheme
        int a = (int) (255 * ((double) i) / (maxIterations / 4));
        return
                // Red & black with fade, a classic!
//                 ( (2*a<<16) );
                // Other options of varying qualities...
                // Hot pink bar & black
//                 ( (255 * (i/15)) << 16 | (255 * (i/15)) );
                // Red bars & black
//                 ((255 * (i/20)) << 16 | 0 | 0 );
                // The cow level! Black & white bars
//                 ((255 * (i/10)) << 16 | (255 * (i/10)) << 8 | (255 * (i/10)) );
                // Blue, blue-green fade, and black
//                 (65536 + i*256 + i/2+128);
                // Black & purple/pink fade
                ( (0) | (2*a<<16) | (a<<8) | ((a*2)<<0) );
    }
}
