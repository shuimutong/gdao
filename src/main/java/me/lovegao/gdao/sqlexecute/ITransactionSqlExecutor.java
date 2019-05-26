package me.lovegao.gdao.sqlexecute;

/**
 * 包含事务控制的sql执行
 * @author simple
 *
 */
public interface ITransactionSqlExecutor extends ISqlExecutor {
	/**
	 * 开启事务
	 * @throws Exception
	 */
	public void beginTransaction() throws Exception;
	
	/**
	 * 开启事务
	 * @param transactionIsolationLevel 事务传播级别 one of the following <code>Connection</code> constants:
     *        <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
     *        <code>Connection.TRANSACTION_READ_COMMITTED</code>,
     *        <code>Connection.TRANSACTION_REPEATABLE_READ</code>, or
     *        <code>Connection.TRANSACTION_SERIALIZABLE</code>.
     *        (Note that <code>Connection.TRANSACTION_NONE</code> cannot be used
     *        because it specifies that transactions are not supported.)
	 * @throws Exception
	 */
	public void beginTransaction(int transactionIsolationLevel) throws Exception;
	
	/**
	 * 提交事务
	 * @throws Exception
	 */
	public void commitTransaction() throws Exception;
	
	/**
	 * 回滚事务
	 * @throws Exception
	 */
	public void rollbackTransaction() throws Exception;
}
