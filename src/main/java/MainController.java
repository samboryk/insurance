
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainController {

    private final BranchDAO branchDAO = new BranchDAO();
    private final InsuranceTypeDAO typeDAO = new InsuranceTypeDAO();
    private final ContractDAO contractDAO = new ContractDAO();

    // --- Філії ---
    @FXML private TableView<Branch> branchTable;
    @FXML private TableColumn<Branch, String> branchColName, branchColAddress, branchColPhone;
    @FXML private TextField tfBranchName, tfBranchAddress, tfBranchPhone;

    // --- Види страхування ---
    @FXML private TableView<InsuranceType> typeTable;
    @FXML private TableColumn<InsuranceType, String> typeColName;
    @FXML private TextField tfTypeName;

    // --- Договори ---
    @FXML private TableView<Contract> contractTable;
    @FXML private TableColumn<Contract, String> colContractNum, colContractDate, colContractSum, colContractRate, colContractPremium, colContractBranch, colContractType;
    @FXML private TextField tfContractNum, tfContractSum, tfContractRate;
    @FXML private DatePicker dpContractDate;
    @FXML private ComboBox<Branch> cbBranch;
    @FXML private ComboBox<InsuranceType> cbType;

    // --- Звіт ---
    @FXML private Label lblTotalSum, lblTotalPremium;

    @FXML
    public void initialize() {
        // Налаштування колонок Філій
        branchColName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        branchColAddress.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        branchColPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        branchTable.getItems().addAll(branchDAO.findAll());

        // Налаштування колонок Видів страхування
        typeColName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        typeTable.getItems().addAll(typeDAO.findAll());

        // Налаштування колонок Договорів
        colContractNum.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContractNumber()));
        colContractDate.setCellValueFactory(data -> {
            Date d = data.getValue().getConclusionDate();
            return new SimpleStringProperty(d == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(d));
        });
        colContractSum.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getInsuranceSum())));
        colContractRate.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.4f", data.getValue().getTariffRate())));
        colContractPremium.setCellValueFactory(data -> {
            double premium = data.getValue().getInsuranceSum() * data.getValue().getTariffRate();
            return new SimpleStringProperty(String.format("%.2f", premium));
        });
        colContractBranch.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBranch().getName()));
        colContractType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInsuranceType().getName()));

        contractTable.getItems().addAll(contractDAO.findAll());
        cbBranch.getItems().addAll(branchDAO.findAll());
        cbType.getItems().addAll(typeDAO.findAll());
    }

    @FXML
    private void handleAddBranch() {
        if (!tfBranchName.getText().trim().isEmpty()) {
            Branch b = new Branch(tfBranchName.getText().trim(), tfBranchAddress.getText().trim(), tfBranchPhone.getText().trim());
            branchDAO.save(b);
            branchTable.getItems().add(b);
            cbBranch.getItems().add(b); // Оновлюємо список у договорах
            tfBranchName.clear(); tfBranchAddress.clear(); tfBranchPhone.clear();
        }
    }

    @FXML
    private void handleDeleteBranch() {
        Branch selected = branchTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            branchDAO.delete(selected.getId());
            branchTable.getItems().remove(selected);
            cbBranch.getItems().remove(selected);
        }
    }

    @FXML
    private void handleAddType() {
        if (!tfTypeName.getText().trim().isEmpty()) {
            InsuranceType t = new InsuranceType(tfTypeName.getText().trim());
            typeDAO.save(t);
            typeTable.getItems().add(t);
            cbType.getItems().add(t); // Оновлюємо список у договорах
            tfTypeName.clear();
        }
    }

    @FXML
    private void handleDeleteType() {
        InsuranceType selected = typeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            typeDAO.delete(selected.getId());
            typeTable.getItems().remove(selected);
            cbType.getItems().remove(selected);
        }
    }

    @FXML
    private void handleAddContract() {
        if (tfContractNum.getText().trim().isEmpty() || dpContractDate.getValue() == null ||
                cbBranch.getValue() == null || cbType.getValue() == null) return;

        try {
            Contract c = new Contract();
            c.setContractNumber(tfContractNum.getText().trim());
            c.setConclusionDate(java.sql.Date.valueOf(dpContractDate.getValue()));
            c.setInsuranceSum(Double.parseDouble(tfContractSum.getText().trim()));
            c.setTariffRate(Double.parseDouble(tfContractRate.getText().trim()));
            c.setBranch(cbBranch.getValue());
            c.setInsuranceType(cbType.getValue());

            contractDAO.save(c);
            contractTable.getItems().add(c);

            tfContractNum.clear(); dpContractDate.setValue(null);
            tfContractSum.clear(); tfContractRate.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteContract() {
        Contract selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            contractDAO.delete(selected.getId());
            contractTable.getItems().remove(selected);
        }
    }

    @FXML
    private void handleRefreshReport() {
        List<Contract> contracts = contractDAO.findAll();
        double sum = 0, premium = 0;
        for (Contract c : contracts) {
            sum += c.getInsuranceSum();
            premium += (c.getInsuranceSum() * c.getTariffRate());
        }
        lblTotalSum.setText(String.format("Загальна страхова сума: %.2f грн", sum));
        lblTotalPremium.setText(String.format("Загальна премія: %.2f грн", premium));
    }
}