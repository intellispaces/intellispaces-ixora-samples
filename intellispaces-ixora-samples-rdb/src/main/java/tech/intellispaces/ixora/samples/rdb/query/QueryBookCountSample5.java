package tech.intellispaces.ixora.samples.rdb.query;

import tech.intellispaces.commons.action.Action;
import tech.intellispaces.commons.action.Actions;
import tech.intellispaces.jaquarius.annotation.Inject;
import tech.intellispaces.jaquarius.annotation.Module;
import tech.intellispaces.jaquarius.annotation.Startup;
import tech.intellispaces.jaquarius.ixora.cli.CliConfiguration;
import tech.intellispaces.jaquarius.ixora.cli.MovableConsoleHandle;
import tech.intellispaces.jaquarius.ixora.data.association.IxoraDictionaryToDataGuide;
import tech.intellispaces.jaquarius.ixora.data.snakeyaml.SnakeyamlGuide;
import tech.intellispaces.jaquarius.ixora.hikaricp.configuration.HikariCpConfiguration;
import tech.intellispaces.jaquarius.ixora.rdb.configuration.RdbConfiguration;
import tech.intellispaces.jaquarius.ixora.rdb.statement.MovableResultSetHandle;
import tech.intellispaces.jaquarius.ixora.rdb.transaction.MovableTransactionFactoryHandle;
import tech.intellispaces.jaquarius.ixora.rdb.transaction.TransactionalAction;
import tech.intellispaces.jaquarius.ixora.rdb.transaction.Transactions;
import tech.intellispaces.jaquarius.system.Modules;

@Module({
    CliConfiguration.class,
    RdbConfiguration.class,
    HikariCpConfiguration.class,
    SnakeyamlGuide.class,
    IxoraDictionaryToDataGuide.class
})
public abstract class QueryBookCountSample5 {

  /**
   * This method will be invoked automatically after the module is started.<p/>
   *
   * The values of method arguments will be injected automatically.
   *
   * @param transactionFactory transaction factory.
   * @param console value of the projection named 'console'.
   */
  @Startup
  public void startup(@Inject MovableTransactionFactoryHandle transactionFactory, @Inject MovableConsoleHandle console) {
    Action action = Actions.get(() -> {
      MovableResultSetHandle rs = Transactions.current().query(Queries.BOOK_COUNT);
      rs.next();
      console.print("Number books: ");
      console.println(rs.integer32Value("count"));
    });
    var transactionalAction = new TransactionalAction(transactionFactory, action);
    transactionalAction.execute();
  }

  /**
   * In the main method, we load and run the IntelliSpaces framework module.
   */
  public static void main(String[] args) {
    Modules.load(QueryBookCountSample5.class, args).start();
  }
}
