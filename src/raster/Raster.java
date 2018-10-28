package raster;

import java.util.Optional;

public interface Raster <PixelType> {
	
	Optional<PixelType> getPixel (int x, int y);
	void setPixel (int x, int y, PixelType value);
	int getWidth();
	int getHeight();
	void clear(PixelType value);
}
