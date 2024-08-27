package intellispaces.samples.rdb;

import intellispaces.core.IntellispacesFramework;
import intellispaces.core.annotation.Inject;
import intellispaces.core.annotation.Module;
import intellispaces.core.annotation.Startup;
import intellispaces.ixora.cli.CliConfiguration;
import intellispaces.ixora.cli.Console;
import intellispaces.ixora.hikary.HikariConfiguration;
import intellispaces.ixora.rdb.RdbConfiguration;
import intellispaces.ixora.rdb.ResultSet;
import intellispaces.ixora.rdb.Transaction;
import intellispaces.ixora.rdb.TransactionFactory;
import intellispaces.ixora.rdb.annotation.Transactional;
import intellispaces.ixora.snakeyaml.YamlStringToPropertiesSnakeyamlMapper;
import intellispaces.ixora.structures.collection.List;
import intellispaces.ixora.structures.properties.PropertiesToDataIxoraMapper;

@Module(units = {
    CliConfiguration.class,
    RdbConfiguration.class,
    HikariConfiguration.class,
    YamlStringToPropertiesSnakeyamlMapper.class,
    PropertiesToDataIxoraMapper.class
})
public abstract class QueryBookRevenueSample2 {

  /**
   * This method returns projection named 'transactionFactory'.<p/>
   *
   * Implementation of this method will be auto generated.
   */
  @Inject
  public abstract TransactionFactory transactionFactory();

  /**
   * This method will be invoked automatically after the module is started.<p/>
   *
   * The values of method arguments will be injected automatically.
   *
   * @param console value of the projection named 'console'.
   */
  @Startup
  @Transactional
  public void startup(@Inject Console console, @Inject Transaction tx) {
    ResultSet rs = tx.query(Sqls.QUERY_BOOK_REVENUE_SQL);
    List<BookRevenueProjection> bookRevenues = rs.values(BookRevenueProjection.class);
    for (BookRevenueProjection bookRevenue : bookRevenues.nativeList()) {
      console.print("Book title: ");
      console.print(bookRevenue.title());
      console.print(". Revenue: ");
      console.println(bookRevenue.revenue() != null ?  bookRevenue.revenue() : 0);
    }
  }

  /**
   * In the main method, we load and run the IntelliSpaces framework module.
   */
  public static void main(String[] args) {
    IntellispacesFramework.loadModule(QueryBookRevenueSample2.class, args);
  }
}
