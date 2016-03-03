package es.udc.riws.recomendacion.ratings;

import java.io.File;
import java.io.IOException;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import es.udc.riws.recomendacion.types.Film;
import es.udc.riws.recomendacion.types.Rating;
import es.udc.riws.recomendacion.types.User;

public class FilmsRatings extends BaseRatings{
	
	public FilmsRatings(final String filename, final int sheet, final int columnLimit) {
		super();
		loadRatings(filename, sheet, columnLimit);
	}
	
	private void loadRatings(final String filename, final int sheetNumber, final int columnLimit) {
		final File doc = new File(filename);
		try {
			final Sheet sheet = SpreadSheet.createFromFile(doc).getSheet(sheetNumber);
			for (int col = 2; col < sheet.getColumnCount() - columnLimit; col++) {
				users.add(new User(sheet.getCellAt(col, 0).getTextValue()));
			}
			
			for (int row = 1; row < sheet.getRowCount(); row++) {
				final Film film = new Film(sheet.getCellAt(1, row).getTextValue());
				this.films.add(film);
				for (int col = 2; col < sheet.getColumnCount() - columnLimit; col++) {
					String cellText = sheet.getCellAt(col, row).getTextValue().trim();
					cellText = cellText.replace(',', '.'); //Reemplazar las , por . para realizar la conversión
					Double rating = cellText.length() > 0 ? Double.valueOf(cellText) : 0.0;
					this.ratings.add(new Rating(film, users.get(col - 2), rating));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
