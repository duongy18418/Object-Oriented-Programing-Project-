/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convertjavahtmlgui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

/**
 *
 * @author yduong
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea textArea;
    @FXML
    private Button chooseFile;
    @FXML
    private Button clear;
    @FXML
    private CheckBox createHTMLFile;
    @FXML
    private CheckBox overwrite;

    @Override

    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onChooseFile(ActionEvent event) {
        List<File> fileList = null;
        convertJavaToHTML(fileList);
    }

    @FXML
    private void onClear(ActionEvent event) {
        textArea.clear();
    }

    private void convertJavaToHTML(List<File> fileList) {
        FileChooser inputFile = new FileChooser();
        FileChooser.ExtensionFilter javaExtensions = new FileChooser.ExtensionFilter("Java file", "*.java");
        inputFile.getExtensionFilters().add(javaExtensions);
        fileList = inputFile.showOpenMultipleDialog(null);

        if (fileList != null) {
            for (File inFile : fileList) {
                if (inFile.length() == 0) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("File " + inFile.getName() + " is empty. Please choose another file.");
                    alert.showAndWait();

                } else {
                    printToTextArea(inFile);
                    if (createHTMLFile.isSelected()) {
                        try {
                            File outFile = null;
                            createHTMLFile(inFile, outFile);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    private void printToTextArea(File inFile) {
        try {
            Scanner scan = new Scanner(inFile);
            textArea.appendText("<!DOCTYPE html>\n<html><body>\n");
            textArea.appendText("<h1>Java Source Code</h1>\n");
            textArea.appendText("<h2>" + inFile.getName() + "</h2>\n");
            textArea.appendText("<pre>\n");
            while (scan.hasNext()) {

                textArea.appendText(replace(scan.nextLine()) + "\n");
            }
            
            textArea.appendText("</pre></body></html>\n");

            scan.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createHTMLFile(File inFile, File outFile) throws FileNotFoundException {
        String inputFileName = inFile.getName();
        String newFileName = inputFileName.substring(0, inputFileName.lastIndexOf(".")) + ".html";
        outFile = new File(inFile.getParent(), newFileName);
        if (outFile.exists()) {
            overwriteFile(inFile, outFile);
        } else {
            writeToFile(inFile, outFile);
        }
    }

    private void writeToFile(File inFile, File outFile) {
        try {
            FileOutputStream newFile = new FileOutputStream(outFile, false);
            FileWriter writer = new FileWriter(outFile);
            writer.write("<!DOCTYPE html>\n<html><body>");
            writer.write("<h1>Java Source Code</h1>\n");
            writer.write("<h2>" + inFile.getName() + "</h2>");
            writer.write("<pre>");
            Scanner scan = new Scanner(inFile);
            while (scan.hasNextLine()) {
                writer.write(replace(scan.nextLine()));
                writer.write("\n");
            }
            scan.close();
            writer.write("</pre></body></html>");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String replace(String str) {
        str = str.replace("\t", "   ")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        return str;
    }

    private void overwriteFile(File inFile, File outFile) {
        if (!overwrite.isSelected()) {
            try {
                FileOutputStream newFile = new FileOutputStream(outFile, true);
                FileWriter writer = new FileWriter(outFile, true);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            writeToFile(inFile, outFile);
        }
    }
}
