/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcpuemulator;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 *
 * @author mzp7
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TableView<TableCell> table;
    @FXML
    private TextField addresstext;
    @FXML
    private TextField valuetext;
    @FXML
    private StackPane codepane;
    
    private CodeArea codeArea;

    class TableCell {
        public int address;
        public int value;
        public Instruction inst = null;
    }

    class Instruction {
        public String opcode;
        public String addressA;
        public String addressB;
        Integer inst = new Integer(0);

        public Instruction(String source) {
            if (source.matches("^.*\\w+\\s\\d+\\s\\d+$")) {
                String[] parts = source.trim().split(" ");
                opcode = parts[0];
                addressA = parts[1];
                addressB = parts[2];
                
                generateBytes();
            } else {
                throw new IllegalArgumentException();
            }
        }

        public int getAddressA() {
            return Integer.valueOf(this.addressA);
        }

        public int getAddressB() {
            return Integer.valueOf(this.addressB);
        }
        
        private void generateBytes(){
            int intaddressA, intaddressB;
            try {
                intaddressA = Integer.valueOf(addressA);
                intaddressB = Integer.valueOf(addressB);
            } catch (Exception e){
                System.out.println("hcpuemulator.FXMLDocumentController.Instruction.generateBytes()" + e);
                inst = null;
                return;
            }
            
            switch (opcode){
                case "ADD":
                    // opcode and steering bit are zero
                    break;
                case "ADDi":
                    inst += 1 * (int) Math.pow(2, 28);
                    break;
                case "NAND":
                    inst += 2 * (int) Math.pow(2, 28);
                    break;
                case "NANDi":
                    inst += 3 * (int) Math.pow(2, 28);
                    break;
                case "SRL":
                    inst += 4 * (int) Math.pow(2, 28);
                    break;
                case "SRLi":
                    inst += 5 * (int) Math.pow(2, 28);
                    break;
                case "LT":
                    inst += 6 * (int) Math.pow(2, 28);
                    break;
                case "LTi":
                    inst += 7 * (int) Math.pow(2, 28);
                    break;
                case "CP":
                    inst += 8 * (int) Math.pow(2, 28);
                    break;
                case "CPi":
                    inst += 9 * (int) Math.pow(2, 28);
                    break;
                case "CPI":
                    inst += 10 * (int) Math.pow(2, 28);
                    break;
                case "CPIi":
                    inst += 11 * (int) Math.pow(2, 28);
                    break;
                case "BZJ":
                    inst += 12 * (int) Math.pow(2, 28);
                    break;
                case "BZJi":
                    inst += 13 * (int) Math.pow(2, 28);
                    break;
                case "MUL":
                    inst += 14 * (int) Math.pow(2, 28);
                    break;
                case "MULi":
                    inst += 15 * (int) Math.pow(2, 28);
                    break;
            }
            
            inst += intaddressA * (int) Math.pow(2, 14) + intaddressB;
        }
        
        public void run(Integer pc) {
            Instruction inst = this;
            switch (inst.opcode) {
                case "ADD":
                    setValue(inst.getAddressA(), getValue(inst.getAddressA()) + getValue(inst.getAddressB()));
                    break;
                case "ADDi":
                    setValue(inst.getAddressA(), getValue(inst.getAddressA()) + inst.getAddressB());
                    break;
                case "NAND":
                    setValue(inst.getAddressA(), ~(getValue(inst.getAddressA()) & getValue(inst.getAddressB())));
                    break;
                case "NANDi":
                    setValue(inst.getAddressA(), ~(getValue(inst.getAddressA()) & inst.getAddressB()));
                    break;
                case "SRL":
                    setValue(inst.getAddressA(), getValue(inst.getAddressA()) >> (getValue(inst.getAddressB() % 32)));
                    break;
                case "SRLi":
                    setValue(inst.getAddressA(), getValue(inst.getAddressA()) << (getValue(inst.getAddressB() % 32)));
                    break;
                case "LT":
                    if (getValue(inst.getAddressA()) < getValue(inst.getAddressB())) {
                        setValue(inst.getAddressA(), 1);
                    } else {
                        setValue(inst.getAddressA(), 0);
                    }
                    break;
                case "LTi":
                    if (getValue(inst.getAddressA()) < inst.getAddressB()) {
                        setValue(inst.getAddressA(), 1);
                    } else {
                        setValue(inst.getAddressA(), 0);
                    }
                    break;
                case "CP":
                    setValue(inst.getAddressA(), getValue(inst.getAddressB()));
                    break;
                case "CPi":
                    setValue(inst.getAddressA(), inst.getAddressB());
                    break;
                case "CPI":
                    setValue(inst.getAddressA(), getValue(getValue(inst.getAddressB())));
                    break;
                case "CPIi":
                    setValue(getValue(inst.getAddressA()), getValue(inst.getAddressB()));
                    break;
                case "BZJ":
                    if (getValue(inst.getAddressB()) == 0) {
                        pc = getValue(inst.getAddressA());
                        pc--;
                    }
                    break;
                case "BZJi":
                    pc = getValue(inst.getAddressA()) + inst.getAddressB();
                    pc--;
                    break;
                case "MUL":
                    setValue(inst.getAddressA(), getValue(inst.getAddressA()) * getValue(inst.getAddressB()));
                    break;
                case "MULi":
                    setValue(inst.getAddressA(), getValue(inst.getAddressA()) * inst.getAddressB());
                    break;
                default:
                    System.err.println("Unknown OP : " + inst.opcode);
            }
            table.refresh();
        }
    }

    private HashMap<Integer, TableCell> memory = new HashMap<>(256);

    @FXML
    public void openhelppage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("InfoPage.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error at openhelppage | " + e);
        }
    }

    @FXML
    public void runscript(ActionEvent event) {
        Scanner input = new Scanner(codeArea.getText());
        
        for (int i = 0; input.hasNext(); i++) {
            setValue(i, input.nextLine());
        }
        
        // run script
        Integer pc = 0;
        Instruction instruction = null;
        
        while (true) {            
            TableCell cell = memory.get(pc);
            instruction = cell != null ? cell.inst : null;
            
            if (instruction == null){ break; }
            
            instruction.run(pc);
            pc++;
        }
    }

    @FXML
    public void clearmemory() {
        table.getItems().clear();
        memory.clear();
    }

    @FXML
    public void insertToMemory() {
        if (addresstext.getText().length() > 0 && valuetext.getText().length() > 0) {
            setValue(Integer.valueOf(addresstext.getText()), Integer.valueOf(valuetext.getText()));
            table.refresh();
        }
    }

    public TableCell allocateMemory(int bbuff) {
        if (bbuff >= 0 && bbuff < Math.pow(2, 32)) {
            if (memory.get(bbuff) != null){
                if (memory.get(bbuff).inst != null){
                    System.err.println("Code allocate the memory location which contains instraction");
                }
            }
            
            TableCell newCell = new TableCell();
            newCell.address = bbuff;
            memory.put(newCell.address, newCell);
            table.getItems().add(newCell);
            return newCell;
        } else {
            throw new OutOfMemoryError();
        }
    }

    public int getValue(int bbuff) {
        if (memory.containsKey(bbuff)) {
            TableCell cell = memory.get(bbuff);
            
            if (cell.inst != null){
                return cell.inst.inst;
            } else {
                return cell.value;
            }
        } else {
            return allocateMemory(bbuff).value;
        }
    }

    public void setValue(int bbuff, int value) {
        if (memory.containsKey(bbuff)) {
            TableCell cell = memory.get(bbuff);
            cell.value = value;
        } else {
            allocateMemory(bbuff).value = value;
        }
    }
    
    public void setValue(int bbuff, String s) {
        if (memory.containsKey(bbuff)) {
            TableCell cell = memory.get(bbuff);
            cell.inst = new Instruction(s);
            cell.value = cell.inst.inst;
        } else {
            TableCell newcell = allocateMemory(bbuff);
            newcell.inst = new Instruction(s);
            newcell.value = newcell.inst.inst;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codepane.getChildren().add(new VirtualizedScrollPane<>(codeArea));
        
        addresstext.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    addresstext.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        valuetext.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    valuetext.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        TableColumn<TableCell, Integer> addresscolumn = new TableColumn<>("Addresses");
        TableColumn<TableCell, Integer> valuecolumn = new TableColumn<>("Values");

        addresscolumn.setCellValueFactory((param) -> {
            return new ReadOnlyIntegerWrapper(param.getValue().address).asObject(); //To change body of generated lambdas, choose Tools | Templates.
        });

        valuecolumn.setCellValueFactory((param) -> {
            if (param.getValue().inst != null){
                return new ReadOnlyIntegerWrapper(param.getValue().inst.inst).asObject();
            }
            return new ReadOnlyIntegerWrapper(param.getValue().value).asObject(); //To change body of generated lambdas, choose Tools | Templates.
        });

        table.getColumns().addAll(addresscolumn, valuecolumn);
    }

}
