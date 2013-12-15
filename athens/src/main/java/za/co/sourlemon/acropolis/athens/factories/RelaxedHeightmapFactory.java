/*
 * The MIT License
 *
 * Copyright 2013 Sour Lemon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package za.co.sourlemon.acropolis.athens.factories;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import za.co.sourlemon.acropolis.athens.components.Heightmap;
import static za.co.sourlemon.acropolis.athens.factories.HeightmapFactory.HEIGHTMAP_DIR;
import static za.co.sourlemon.acropolis.athens.factories.HeightmapFactory.TEX_EXT;

/**
 *
 * @author Daniel
 */
public class RelaxedHeightmapFactory extends HeightmapFactory
{
    public static final int ITERATIONS = 25;
    
    public static int clamp(int v, int min, int max)
    {
        if (v < min) return min;
        if (v >= max) return max-1;
        return v;
    }

    @Override
    public Heightmap create(HeightmapFactoryRequest request)
    {
        
        try
        {
            BufferedImage image = ImageIO.read(new File(HEIGHTMAP_DIR + "/" + request.heightmap + "." + TEX_EXT));

            int w = image.getWidth();
            int l = image.getHeight();
            int bits = image.getColorModel().getComponentSize()[0];
            if (bits == 16)
            {
                return new HeightmapFactory().create(request);
            }
            Heightmap heightmap = new Heightmap(w, l);
            int[] data = image.getRaster().getSamples(0, 0, w, l, 0, new int[w * l]);

            int[][] hm = new int[w][l];

            int i;
            for (int x = 0; x < w; x++)
            {
                for (int z = 0; z < l; z++)
                {
                    i = x + z * w;
                    hm[x][z] = (data[i] << 8) | 0x77;
                }
            }
            
            int avg;
            int top, ntop;
            for (int it = 0; it < ITERATIONS; it++)
            {
                for (int x = 0; x < w; x++)
                {
                    for (int z = 0; z < l; z++)
                    {
                        // TODO: get rid of magic numbers!
                        avg = 0;
                        top = hm[x][z] >> 8;
                        for (int x2 = x-1; x2 <= x+1; x2++)
                        {
                            for (int z2 = z-1; z2 <= z+1; z2++)
                            {
                                avg += hm[clamp(x2,0,w)][clamp(z2,0,l)];
                            }
                        }
                        avg /= (3*3);

                        ntop = avg >> 8;
                        if (ntop > top) hm[x][z] = hm[x][z] | 0x00FF;
                        else if (ntop < top) hm[x][z] = hm[x][z] & 0xFF00;
                        else
                            hm[x][z] = avg;
                    }
                }
            }
            
            float max = (float)(1 << 16);
            
            for (int x = 0; x < w; x++)
            {
                for (int z = 0; z < l; z++)
                {
                    heightmap.heights[x][z] = (float)hm[x][z]/max;
                }
            }
            
            return heightmap;
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
