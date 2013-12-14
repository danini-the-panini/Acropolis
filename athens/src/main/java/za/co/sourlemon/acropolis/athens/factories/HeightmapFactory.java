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
import za.co.sourlemon.acropolis.ems.ComponentFactory;

/**
 *
 * @author Daniel
 */
public class HeightmapFactory implements ComponentFactory<Heightmap, HeightmapFactoryRequest>
{
    public static final String TEX_EXT = "png";
    public static final String HEIGHTMAP_DIR = "textures/heightmaps";

    @Override
    public Heightmap create(HeightmapFactoryRequest request)
    {
        try
        {
            BufferedImage image = ImageIO.read(new File(HEIGHTMAP_DIR+"/"+request.heightmap+"."+TEX_EXT));
            
            int w = image.getWidth();
            int l = image.getHeight();
            Heightmap hm = new Heightmap(w, l);
            float[] data = image.getRaster().getSamples(0, 0, w, l, 0, new float[0]);
            int i;
            for (int x = 0; x < w; x++)
            {
                for (int z = 0; z < l; z++)
                {
                    i = x+z*w;
                    hm.heights[x][z] = data[i];
                }
            }
            return hm;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
}
