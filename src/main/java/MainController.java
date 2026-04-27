import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainController {
    private final BranchDAO branchDAO = new BranchDAO();
    private final InsuranceTypeDAO typeDAO = new InsuranceTypeDAO();
    private final ContractDAO contractDAO = new ContractDAO();

    @FXML private TableView<Branch> branchTable;
    @FXML private TableColumn<Branch, String> branchColName, branchColAddress, branchColPhone;
    @FXML private TextField tfBranchName, tfBranchAddress, tfBranchPhone;

    @FXML private TableView<InsuranceType> typeTable;
    @FXML private TableColumn<InsuranceType, String> typeColName;
    @FXML private TextField tfTypeName;

    @FXML private TableView<Contract> contractTable;
    @FXML private TableColumn<Contract, String> colContractNum, colContractDate, colContractSum, colContractRate, colContractPremium, colContractBranch, colContractType;
    @FXML private TextField tfContractNum, tfContractSum, tfContractRate;
    @FXML private DatePicker dpContractDate;
    @FXML private ComboBox<Branch> cbBranch;
    @FXML private ComboBox<InsuranceType> cbType;

    @FXML private Label lblTotalSum, lblTotalPremium;

    @FXML public void initialize() { setupTables(); handleRefresh(); }

    private void setupTables() {
        branchColName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        branchColAddress.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        branchColPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));

        typeColName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        colContractNum.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContractNumber()));
        colContractDate.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getConclusionDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(data.getValue().getConclusionDate())));
        colContractSum.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getInsuranceSum())));
        colContractRate.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.4f", data.getValue().getTariffRate())));
        colContractPremium.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%.2f", data.getValue().getInsuranceSum() * data.getValue().getTariffRate())));
        colContractBranch.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBranch().getName()));
        colContractType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInsuranceType().getName()));
    }

    @FXML private void handleRefresh() {
        try {
            List<Branch> branches = branchDAO.findAll();
            branchTable.getItems().setAll(branches); cbBranch.getItems().setAll(branches);
            List<InsuranceType> types = typeDAO.findAll();
            typeTable.getItems().setAll(types); cbType.getItems().setAll(types);
            contractTable.getItems().setAll(contractDAO.findAll());
            handleRefreshReport();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleAddBranch() {
        if (tfBranchName.getText().isEmpty()) return;
        branchDAO.save(new Branch(tfBranchName.getText(), tfBranchAddress.getText(), tfBranchPhone.getText()));
        tfBranchName.clear(); tfBranchAddress.clear(); tfBranchPhone.clear(); handleRefresh();
    }
    @FXML private void handleDeleteBranch() {
        Branch selected = branchTable.getSelectionModel().getSelectedItem();
        if (selected != null) { branchDAO.delete(selected.getId()); handleRefresh(); }
    }
    @FXML private void handleAddType() {
        if (tfTypeName.getText().isEmpty()) return;
        typeDAO.save(new InsuranceType(tfTypeName.getText()));
        tfTypeName.clear(); handleRefresh();
    }
    @FXML private void handleDeleteType() {
        InsuranceType selected = typeTable.getSelectionModel().getSelectedItem();
        if (selected != null) { typeDAO.delete(selected.getId()); handleRefresh(); }
    }
    @FXML private void handleAddContract() {
        if (tfContractNum.getText().isEmpty() || dpContractDate.getValue() == null || cbBranch.getValue() == null || cbType.getValue() == null) return;
        try {
            Contract c = new Contract(); c.setContractNumber(tfContractNum.getText());
            c.setConclusionDate(java.sql.Date.valueOf(dpContractDate.getValue()));
            c.setInsuranceSum(Double.parseDouble(tfContractSum.getText()));
            c.setTariffRate(Double.parseDouble(tfContractRate.getText()));
            c.setBranch(cbBranch.getValue()); c.setInsuranceType(cbType.getValue());
            contractDAO.save(c);
            tfContractNum.clear(); dpContractDate.setValue(null); tfContractSum.clear(); tfContractRate.clear();
            handleRefresh();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    @FXML private void handleDeleteContract() {
        Contract selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected != null) { contractDAO.delete(selected.getId()); handleRefresh(); }
    }
    @FXML private void handleRefreshReport() {
        double sum = 0, premium = 0;
        for (Contract c : contractDAO.findAll()) {
            sum += c.getInsuranceSum(); premium += (c.getInsuranceSum() * c.getTariffRate());
        }
        lblTotalSum.setText(String.format("Загальна страхова сума: %.2f грн", sum));
        lblTotalPremium.setText(String.format("Загальна премія: %.2f грн", premium));
    }
}