package com.springboot.autoconfig.alimns.executor;


public class MessageHistorySqls {

	public static final String sql_message_send_history_insert = "insert into %s (id,mns_ref,message_tag,message_txt,create_time,status) values(?,?,?,?,?,?)";

	public static final String sql_message_send_history_update = "update %s set last_send_time=?, status=?, tries=? where id=?";

	public static final String sql_message_receive_history_status = "select id, status, consume_time, consume_tries from %s where id=?";

	public static final String sql_message_receive_history_insert = "insert into %s (id, message_txt, recv_time, consume_time, consume_tries, status, mns_ref) values(?,?,?,?,?,?,?)";

	public static final String sql_message_receive_history_update = "update %s set consume_time=?, consume_tries=?, status=? where id=? and consume_time=? and status=?";

	public static final String sql_message_receive_history_update_status = "update %s set status=? where id=? ";
}
