package com.shatteredpixel.shatteredpixeldungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.gltextures.SmartTexture;

import java.util.Arrays;

public class LightGrid {

    private int width;
    private int height;

    private float[] rGrid;
    private float[] gGrid;
    private float[] bGrid;

    private Pixmap pixmap;
    private SmartTexture lightmapTexture;

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;

        rGrid = new float[size];
        gGrid = new float[size];
        bGrid = new float[size];

        if (pixmap != null) pixmap.dispose();
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        lightmapTexture = new SmartTexture(pixmap);
        lightmapTexture.filter(GL20.GL_LINEAR, GL20.GL_LINEAR);
    }

    public void clear(float ambientR, float ambientG, float ambientB) {
        Arrays.fill(rGrid, ambientR);
        Arrays.fill(gGrid, ambientG);
        Arrays.fill(bGrid, ambientB);
    }

    public void addLight(float tileX, float tileY, float r, float g, float b, float radius, float decayFactor) {
        Level level = Dungeon.level;
        if (level == null) return;

        int minX = Math.max(0, (int) (tileX - radius));
        int maxX = Math.min(width - 1, (int) (tileX + radius));
        int minY = Math.max(0, (int) (tileY - radius));
        int maxY = Math.min(height - 1, (int) (tileY + radius));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                float dx = x - tileX;
                float dy = y - tileY;
                float distSq = dx * dx + dy * dy;
                float radSq = radius * radius;

                if (distSq <= radSq) {
                    if (hasLineOfSight(tileX, tileY, x, y, level)) {
                        float attenuation = 1.0f / (1.0f + decayFactor * distSq);
                        float cutoff = 1.0f / (1.0f + decayFactor * radSq);
                        float intensity = Math.max(0, (attenuation - cutoff) / (1.0f - cutoff));

                        int idx = x + y * width;
                        rGrid[idx] += r * intensity;
                        gGrid[idx] += g * intensity;
                        bGrid[idx] += b * intensity;
                    }
                }
            }
        }
    }

    private boolean hasLineOfSight(float x0, float y0, float x1, float y1, Level level) {
        int cx0 = (int) x0;
        int cy0 = (int) y0;
        int cx1 = (int) x1;
        int cy1 = (int) y1;

        int dx = Math.abs(cx1 - cx0);
        int dy = Math.abs(cy1 - cy0);
        int sx = cx0 < cx1 ? 1 : -1;
        int sy = cy0 < cy1 ? 1 : -1;
        int err = dx - dy;

        int currX = cx0;
        int currY = cy0;

        while (true) {
            if (currX == cx1 && currY == cy1) return true;
            if (currX < 0 || currX >= width || currY < 0 || currY >= height) return false;

            int cell = currX + currY * width;
            if (level.losBlocking[cell] && (currX != cx0 || currY != cy0)) {
                return false;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                currX += sx;
            }
            if (e2 < dx) {
                err += dx;
                currY += sy;
            }
        }
    }

    public SmartTexture updateTexture() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = x + y * width;

                float r = rGrid[idx];
                float g = gGrid[idx];
                float b = bGrid[idx];
                float maxVal = Math.max(r, Math.max(g, b));

                if (maxVal > 1.0f) {
                    r /= maxVal;
                    g /= maxVal;
                    b /= maxVal;
                }

                pixmap.setColor(r, g, b, 1.0f);
                pixmap.drawPixel(x, y);
            }
        }

        lightmapTexture.bind();
        Gdx.gl.glTexSubImage2D(
                GL20.GL_TEXTURE_2D, 0, 0, 0,
                width, height,
                GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE,
                pixmap.getPixels()
        );

        return lightmapTexture;
    }

    public SmartTexture getTexture() {
        return lightmapTexture;
    }
}