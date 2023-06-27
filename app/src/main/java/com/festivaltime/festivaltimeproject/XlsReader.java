package com.festivaltime.festivaltimeproject;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class XlsReader {
    public static List<Cell[]> extractRowsWithKeyword(Context context, String filePath, String keyword) {
        List<Cell[]> rowsWithKeyword = new ArrayList<>();

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        Workbook workbook = null;

        try {
            inputStream = assetManager.open(filePath);
            workbook = Workbook.getWorkbook(inputStream);

            int numSheets = workbook.getNumberOfSheets();

            for (int sheetIndex = 0; sheetIndex < numSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheet(sheetIndex);
                int numRows = sheet.getRows();
                int numCols = sheet.getColumns();

                for (int row = 0; row < numRows; row++) {
                    Cell[] cells = sheet.getRow(row);
                    boolean keywordFound = false;

                    for (Cell cell : cells) {
                        String cellContent = cell.getContents();

                        if (cellContent.contains(keyword)) {
                            keywordFound = true;
                            break;
                        }
                    }

                    if (keywordFound) {
                        rowsWithKeyword.add(cells);
                    }
                }
            }
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return rowsWithKeyword;
    }
}
