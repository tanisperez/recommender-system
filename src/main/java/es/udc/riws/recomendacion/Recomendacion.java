package es.udc.riws.recomendacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import es.udc.riws.recomendacion.ratings.BaseRatings;
import es.udc.riws.recomendacion.ratings.FilmsRatings;
import es.udc.riws.recomendacion.ratings.PredictionsRatings;
import es.udc.riws.recomendacion.similitud.CosenoSimilitudStrategy;
import es.udc.riws.recomendacion.similitud.PearsonSimilitudStrategy;
import es.udc.riws.recomendacion.similitud.SimilitudStrategy;
import es.udc.riws.recomendacion.types.Pair;
import es.udc.riws.recomendacion.types.Rating;
import es.udc.riws.recomendacion.types.User;
import es.udc.riws.recomendacion.types.Film;

/**
 * Práctica de Recomendación para la asignatura de RIWS.
 * 
 * @author Estanislao R. Pérez Nartallo.
 */
public class Recomendacion 
{
	private final static int SHEET_TRAINING = 3;
	private final static int SHEET_TEST = 4;
	
	private static void showUsage() {
		System.out.println("recomendacion.jar sheet.ods");
		System.out.println("\t-k: Neighbour size (Default 2)");
		System.out.println("\t-sim: [cos|pearson] (Default cos)");
		System.out.println("\t-o: output sheet (No output by default)");
		System.out.println("\t-h: show help");
	}
	
    public static void main( String[] args )
    {
    	List<String> argsList = new ArrayList<String>();  
        List<Pair<String, String>> optsList = new ArrayList<Pair<String, String>>();
        
        if (args.length == 0) {
        	showUsage();
        	return;
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i].charAt(0)) {
            case '-':
                if (args[i].length() < 2) {
                    throw new IllegalArgumentException("Not a valid argument: " + args[i]);
                }
                if (args.length - 1 == i) {
                    throw new IllegalArgumentException("Expected arg after: " + args[i]);
                }
                // -opt
                optsList.add(new Pair<String, String>(args[i], args[i+1]));
                i++;
                break;
            default:
                // arg
                argsList.add(args[i]);
                break;
            }
        }
        
        // Default values
        int k = 2;
        SimilitudStrategy similitudStrategy = new CosenoSimilitudStrategy();
        String output = "";
        
        for (final Pair<String, String> option: optsList) {
        	if (option.getLeftValue().equals("-k")) {
        		try {
        			k = Integer.parseInt(option.getRightValue());
        		} catch (NumberFormatException e) {
        			throw new IllegalArgumentException(e);
        		}
        	} else {
        		if (option.getLeftValue().equals("-sim")) {
        			if (option.getRightValue().equals("cos")) {
        				similitudStrategy = new CosenoSimilitudStrategy();
        			} else {
        				if (option.getRightValue().equals("pearson")) {
        					similitudStrategy = new PearsonSimilitudStrategy();
        				}
        			}
        		} if (option.getLeftValue().equals("-o")) {
        			output = option.getRightValue();
        		} else {
        			if (option.getLeftValue().equals("-h")) {
        				showUsage();
        				return;
        			}
        		}
        	}
        }
        
        String filename = "";
        if (argsList.size() > 0) {
        	filename = argsList.get(0);
        } else {
        	throw new IllegalArgumentException("No sheet specified");
        }
    	
    	// El número de columnas varia entre la hoja de training y test, por eso pongo el 2 y el 1.
    	// En Windows me da problemas modificar el .ods con LibreOffice para borrar la columna de más.
        final BaseRatings trainingRatings = new FilmsRatings(filename, SHEET_TRAINING, 2);
        //System.out.println(trainingRatings);
        final BaseRatings testingRatings = new FilmsRatings(filename, SHEET_TEST, 1);
        //System.out.println(testingRatings);
        
        // Para generar la matriz de similitud se emplea un patrón estrategia que puede ser: CosenoSimilitudesStrategy o PearsonSimilitudStrategy.
        final SimilaritiesMatrix similaritiesMatrix = new SimilaritiesMatrix(trainingRatings, similitudStrategy);
        if (output.length() == 0) {
        	System.out.println("Similarities matrix:");
        	System.out.println(similaritiesMatrix);
        }

        // Se crea la matriz de predicciones a partir de la matriz de entrenamiento y la matriz de similitudes. El último
        // parámetro es el número de vecinos que se van a emplear para realizar las predicciones.
        final BaseRatings predictionsRatings = new PredictionsRatings(trainingRatings, similaritiesMatrix, k);
        if (output.length() == 0) {
        	System.out.println("Predictions:");
        	System.out.println(predictionsRatings);
        }
        
        // Se calcula la MAE y RMSE a partir de la matriz de predicciones y la matriz de testing.
        final PredictionsTest predictionsTest = new PredictionsTest(predictionsRatings, testingRatings);
        System.out.println("MAE: " + predictionsTest.getMAE());
        System.out.println("RMSE: " + predictionsTest.getRMSE());
        
        if (output.length() > 0) {
	        try {
				createSpreedSheet(trainingRatings, testingRatings, similaritiesMatrix, predictionsRatings, predictionsTest, output);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    
    private static void createSpreedSheet(final BaseRatings trainingRatings, final BaseRatings testingRatings, 
    		final SimilaritiesMatrix similaritiesMatrix, final BaseRatings predictionsRatings, 
    		final PredictionsTest predictionsTest, final String filename) throws IOException {
    	final File file = new File(filename);
		SpreadSheet sheet = createTrainingSheet(trainingRatings);
		addTestingSheet(sheet, testingRatings);
		addSimilaritiesMatrix(sheet, trainingRatings, similaritiesMatrix);
		addPredictionsSheet(sheet, predictionsRatings);
		addMAERMSESheet(sheet, predictionsTest);
		sheet.saveAs(file);
    }
    
    private static SpreadSheet createTrainingSheet(final BaseRatings trainingRatings) throws IOException {
    	final Object[][] trainingData = new Object[trainingRatings.getFilms().size()][trainingRatings.getUsers().size() + 1];
    	String[] trainingColumns = new String[trainingRatings.getUsers().size() + 1];
    	trainingColumns[0] = "";
    	for (int i = 1; i <= trainingRatings.getUsers().size(); i++) {
    		trainingColumns[i] = trainingRatings.getUsers().get(i - 1).getUser();
    	}
    	
    	int row = 0;
    	for (final Film film : trainingRatings.getFilms()) {
    		int i = 0;
    		trainingData[row] = new Object[trainingRatings.getUsers().size() + 1];
    		trainingData[row][i++] = film.getFilm();
    		for (final Rating rating: trainingRatings.getRatingsByFilm(film)) {
    			trainingData[row][i++] = rating.getRating();
    		}
    		row++;
    	}

    	TableModel model = new DefaultTableModel(trainingData, trainingColumns);
    	SpreadSheet sheet = null;
		sheet = SpreadSheet.createEmpty(model);
		sheet.getSheet(0).setName("Training");
    	return sheet;
    }
    
    private static void addTestingSheet(final SpreadSheet sheet, final BaseRatings testingRatings) throws IOException {
    	final Sheet testingSheet = sheet.addSheet("Testing");
    	testingSheet.ensureColumnCount(testingRatings.getUsers().size() + 1);
    	testingSheet.ensureRowCount(testingRatings.getFilms().size() + 1);
    	
    	testingSheet.getCellAt(0, 0).setValue("");
    	for (int i = 1; i <= testingRatings.getUsers().size(); i++) {
    		testingSheet.getCellAt(i, 0).setValue(testingRatings.getUsers().get(i - 1).getUser());
    	}
    	
    	int row = 1;
    	for (final Film film : testingRatings.getFilms()) {
    		int i = 1;
    		testingSheet.getCellAt(0, row).setValue(film.getFilm());
    		for (final Rating rating: testingRatings.getRatingsByFilm(film)) {
    			testingSheet.getCellAt(i++, row).setValue(rating.getRating());
    		}
    		row++;
    	}
    }
    
    private static void addSimilaritiesMatrix(final SpreadSheet sheet, final BaseRatings trainingRatings, final SimilaritiesMatrix similaritiesMatrix) throws IOException {
    	final Sheet simSheet = sheet.addSheet("Similarities Matrix");
    	simSheet.ensureColumnCount(trainingRatings.getUsers().size() + 1);
    	simSheet.ensureRowCount(trainingRatings.getUsers().size() + 1);

    	simSheet.getCellAt(0, 0).setValue("");
    	for (int i = 1; i <= trainingRatings.getUsers().size(); i++) {
    		simSheet.getCellAt(i, 0).setValue(trainingRatings.getUsers().get(i - 1).getUser());
    	}
    	
    	int x = 1;
    	int y = 0;
    	for (final User user1: trainingRatings.getUsers()) {
    		simSheet.getCellAt(0, x).setValue(user1.getUser());
    		y = 1;
    		for (final User user2: trainingRatings.getUsers()) {
    			simSheet.getCellAt(y++, x).setValue(similaritiesMatrix.getSimilarities(user1, user2));
    		}
    		x++;
    	}
    }
    
    private static void addPredictionsSheet(final SpreadSheet sheet, final BaseRatings predictionsRatings) throws IOException {
    	final Sheet predictionsSheet = sheet.addSheet("Predictions");
    	predictionsSheet.ensureColumnCount(predictionsRatings.getUsers().size() + 1);
    	predictionsSheet.ensureRowCount(predictionsRatings.getFilms().size() + 1);
    	
    	predictionsSheet.getCellAt(0, 0).setValue("");
    	for (int i = 1; i <= predictionsRatings.getUsers().size(); i++) {
    		predictionsSheet.getCellAt(i, 0).setValue(predictionsRatings.getUsers().get(i - 1).getUser());
    	}
    	
    	int row = 1;
    	for (final Film film : predictionsRatings.getFilms()) {
    		int i = 1;
    		predictionsSheet.getCellAt(0, row).setValue(film.getFilm());
    		for (final Rating rating: predictionsRatings.getRatingsByFilm(film)) {
    			predictionsSheet.getCellAt(i++, row).setValue(rating.getRating() == null ? "?" : rating.getRating());
    		}
    		row++;
    	}
    }
    
    private static void addMAERMSESheet(final SpreadSheet sheet, final PredictionsTest predictionsTest) throws IOException {
    	final Sheet maeSheet = sheet.addSheet("MAE and RMSE");
    	maeSheet.ensureColumnCount(2);
    	maeSheet.ensureRowCount(2);
    	maeSheet.getCellAt(0, 0).setValue("MAE");
    	maeSheet.getCellAt(1, 0).setValue(predictionsTest.getMAE());
    	maeSheet.getCellAt(0, 1).setValue("RMSE");
    	maeSheet.getCellAt(1, 1).setValue(predictionsTest.getRMSE());
    }
    
}
