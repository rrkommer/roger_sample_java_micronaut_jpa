package rogmn;

import java.util.List;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import rogmn.db.Book;
import rogmn.db.Emfac;
import rogmn.db.Genre;

@Controller("/hello")
public class HelloController
{
  static Emfac emfac = Emfac.getEmfac();

  @Get("/")
  public HttpStatus index()
  {
  
    return HttpStatus.OK;
  }
  @Get("/books")
  public List<Book> books()
  {
    return emfac.tx().go(em -> {
     List<Book> books = em.createQuery("select e from Book e", Book.class).getResultList();
     if (books.isEmpty() == true) {
       Genre g = new Genre("Krimi");
       em.persist(g);
       em.flush();
       Book b = new Book("1234123123", "Ein Book", null);
       b.setGenre(g);
       em.persist(b);
       em.flush();
     }
     books = em.createQuery("select e from Book e", Book.class).getResultList();
      return books;
    });
  }
  

}