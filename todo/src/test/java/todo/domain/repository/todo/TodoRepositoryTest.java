package todo.domain.repository.todo;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import todo.domain.model.Todo;

/**
 * Repository Test
 * JdbcTemplateによるデータのセットアップ、比較
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context.xml"})
@Transactional
public class TodoRepositoryTest {

	@Inject
	TodoRepository target;
	
	@Inject
	JdbcTemplate jdbctemplate;
	
	@Before
	public void setUp() throws Exception {
		
		//DBinit
		jdbctemplate.update("DELETE FROM todo");
		
		//DBsetup
		List<Todo> todoList = setUpTabledata();
		for(int i=0; i < todoList.size(); i++) {
			Todo todo = todoList.get(i);
			
			jdbctemplate.update("INSERT INTO todo (todo_id,todo_title,finished,created_at) VALUES(?,?,?,?)"
					,todo.getTodoId()
					,todo.getTodoTitle()
					,todo.isFinished()
					,todo.getCreatedAt()
					);
		}
	}
	
	@Test
	@Rollback
	public void testUpdate() throws Exception {
		
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		String todoId = "cceae402-c5b1-440f-bae2-7bee19dc17fb";
		Todo testDataTodo = getTodoData(todoId);
		
		testDataTodo.setFinished(true);
		
		boolean actTodo = target.update(testDataTodo);
		assertEquals(actTodo, true);
		
		//期待値の作成
		Todo exptodo = new Todo();
		exptodo.setTodoId("cceae402-c5b1-440f-bae2-7bee19dc17fb");
		exptodo.setTodoTitle("one");
		exptodo.setFinished(true);
		String strDate = "2017-10-01 15:39:17.888";
		Date date = sdFormat.parse(strDate);
		exptodo.setCreatedAt(date);
		
		//処理後データの取得
		Todo actTestDataTodo = getTodoData(todoId);
		
		//データ検証
		//date型の表示形式が異なるため、時刻文字列に変換して比較している
		assertEquals(exptodo.getTodoId(), actTestDataTodo.getTodoId());
		assertEquals(exptodo.getTodoTitle(), actTestDataTodo.getTodoTitle());
		assertEquals(exptodo.isFinished(), actTestDataTodo.isFinished());
		assertEquals(sdFormat.format(exptodo.getCreatedAt()) ,sdFormat.format(actTestDataTodo.getCreatedAt()));
		
		
	}
	
	//テーブルデータの作成
	private List<Todo> setUpTabledata() throws Exception{
		
		List<Todo> list = new ArrayList<>();
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		Todo todo1 = new Todo();
		todo1.setTodoId("cceae402-c5b1-440f-bae2-7bee19dc17fb");
		todo1.setTodoTitle("one");
		todo1.setFinished(false);
		String strDate1 = "2017-10-01 15:39:17.888";
		Date date1 = sdFormat.parse(strDate1);
		todo1.setCreatedAt(date1);
		list.add(todo1);
		
		Todo todo2 = new Todo();
		todo2.setTodoId("5dd4ba78-ff5b-423b-aa2a-a07118aeaf90");
		todo2.setTodoTitle("two");
		todo2.setFinished(false);
		String strDate2 = "2017-10-01 15:39:19.981";
		Date date2 = sdFormat.parse(strDate2);
		todo2.setCreatedAt(date2);
		list.add(todo2);
		
		Todo todo3 = new Todo();
		todo3.setTodoId("e3bdb9af-3dde-40b7-b5fb-4b388567ab45");
		todo3.setTodoTitle("three");
		todo3.setFinished(false);
		String strDate3 = "2017-10-01 15:39:28.437";
		Date date3 = sdFormat.parse(strDate3);
		todo3.setCreatedAt(date3);
		list.add(todo3);
		
		return list;
	}
	
	//テスト用元データの取得
	private Todo getTodoData(String todoId) {
		
		String sql = "SELECT * FROM todo WHERE todo_id=?";
		
		Todo todoData = (Todo)jdbctemplate.queryForObject(sql, new Object[] {todoId},
				new RowMapper<Todo>() {
					public Todo mapRow(ResultSet rs, int rownum) throws SQLException {
						Todo todoSql = new Todo();
						
						todoSql.setTodoId(rs.getString("todo_id"));
						todoSql.setTodoTitle(rs.getString("todo_title"));
						todoSql.setFinished(rs.getBoolean("finished"));
						todoSql.setCreatedAt(rs.getTimestamp("created_at"));
					
						return todoSql;
					}
		});
		return todoData;
	}
}
