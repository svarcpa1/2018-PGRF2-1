package raster;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class ImageBuffer implements Raster<Integer>{

	private final int width;
	private final int height;

	public BufferedImage getImage() {
		return image;
	}

	private BufferedImage image;

	public ImageBuffer(BufferedImage image) {
		this.image=image;
		this.width=image.getWidth();
		this.height=image.getHeight();
	}

	@Override
	public void clear(Integer value) {
		Graphics gr = image.getGraphics();
		gr.setColor(new Color(value.intValue()));
		gr.fillRect(0,0,width,height);
	}

	private boolean isInside(int x, int y){
		return ((x>=0 && x<=width)&&(y>=0 && y<=height));
	}
	@Override
	public Optional<Integer> getPixel(int x, int y) {
		Integer i = new Integer(image.getRGB(x, y));
		if(isInside(x,y)) {
			return Optional.of(i);
		}else {
			return Optional.empty();
		}
	}

	@Override
	public void setPixel(int x, int y, Integer value) {
		if(isInside(x,y)) {
			image.setRGB(x, y, value);
		}
	}
	@Override
	public int getWidth() {
		return width;
	}
	@Override
	public int getHeight() {
		return height;
	}
}
