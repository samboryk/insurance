
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_number", nullable = false, unique = true)
    private String contractNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "conclusion_date")
    private Date conclusionDate;

    @Column(name = "insurance_sum")
    private double insuranceSum;

    @Column(name = "tariff_rate")
    private double tariffRate;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "insurance_type_id", nullable = false)
    private InsuranceType insuranceType;

    public Contract() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public Date getConclusionDate() { return conclusionDate; }
    public void setConclusionDate(Date conclusionDate) { this.conclusionDate = conclusionDate; }
    public double getInsuranceSum() { return insuranceSum; }
    public void setInsuranceSum(double insuranceSum) { this.insuranceSum = insuranceSum; }
    public double getTariffRate() { return tariffRate; }
    public void setTariffRate(double tariffRate) { this.tariffRate = tariffRate; }
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    public InsuranceType getInsuranceType() { return insuranceType; }
    public void setInsuranceType(InsuranceType insuranceType) { this.insuranceType = insuranceType; }

    @Override
    public String toString() { return "Договір №" + contractNumber; }
}