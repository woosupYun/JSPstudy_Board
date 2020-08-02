package article.service;

import java.sql.Connection;
import java.util.Date;

import article.dao.ArticleContentDao;
import article.dao.ArticleDao;
import article.model.Article;
import article.model.ArticleContent;
import jdbc.JdbcUtil;
import jdbc.ConnectionProvider;

public class WriteArticleService {
	private ArticleDao articleDao = new ArticleDao();
	private ArticleContentDao contentDao = new ArticleContentDao();

	public Integer write(WriteRequest req) {
		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			Article article = toArticle(req);
			Article savedArticle = articleDao.insert(conn, article);

			if (savedArticle == null) {
				throw new RuntimeException("fail to insert article");
			}

			ArticleContent content = new ArticleContent(savedArticle.getNumber(), req.getContent(), req.getFileName());

			ArticleContent savedContent = contentDao.insert(conn, content);

			if (savedContent == null) {
				throw new RuntimeException("fail to insert article_content");
			}

			conn.commit();
			
			return savedArticle.getNumber();
			
		} catch (Exception e) {
			JdbcUtil.rollback(conn);
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	private Article toArticle(WriteRequest req) {
		Date now = new Date();
		return new Article(null,
				req.getWriter(),
				req.getTitle(),
				now, now, 0);
	}
}
