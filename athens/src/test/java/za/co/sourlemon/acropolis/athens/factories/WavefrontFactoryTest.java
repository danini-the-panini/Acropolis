/*
 * Copyright (c) 2013 Triforce - in association with the University of Pretoria and Epi-Use <Advance/>
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

import za.co.sourlemon.acropolis.athens.factories.WavefrontFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Daniel
 */
public class WavefrontFactoryTest {
    
    @Test
    public void testParseVertex() {
        System.out.println("parse 'v'");
        String string = "7";
        int[] expResult = { 6, -1, -1};
        int[] result = WavefrontFactory.parseVertex(string);
        assertArrayEquals(expResult, result);
        
        System.out.println("parse 'v/vt'");
        string = "7/12";
        expResult = new int[]{ 6, 11, -1};
        result = WavefrontFactory.parseVertex(string);
        assertArrayEquals(expResult, result);
        
        System.out.println("parse 'v//vn'");
        string = "7//12";
        expResult = new int[]{ 6, -1, 11};
        result = WavefrontFactory.parseVertex(string);
        assertArrayEquals(expResult, result);
        
        System.out.println("parse 'v/vt/vn'");
        string = "7/6/12";
        expResult = new int[]{ 6, 5, 11};
        result = WavefrontFactory.parseVertex(string);
        assertArrayEquals(expResult, result);
    }
}
