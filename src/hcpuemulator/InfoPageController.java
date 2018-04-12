/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcpuemulator;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author mzp7
 */
public class InfoPageController implements Initializable{
    
    @FXML
    private Text    textarea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            byte[] bytes = Files.readAllBytes(Paths.get("about.txt"));
            String text = new String(bytes);
            textarea.setText(text);
        } catch (IOException e){
            ((Stage) textarea.getScene().getWindow()).close();
        }
    }
}
