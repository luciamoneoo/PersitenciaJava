package pio.daw.ra8.ejercicios.jpa;

import java.util.NoSuchElementException;
import java.util.Scanner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import pio.daw.ra8.util.JPAUtil;

public class ConsolaLibro {

    private EntityManager em;
    private Scanner sc;

    public ConsolaLibro(){
        EntityManagerFactory emf = JPAUtil.crearEMFWithoutDelete("target/ConsolaLibro.odb");
        this.em = emf.createEntityManager();
        this.sc = new Scanner(System.in);
    }

    public void printMenu(){
        StringBuilder sb = new StringBuilder();
        sb.append("1 - Añade un libro\n");
        sb.append("2 - Lista los libros\n");
        sb.append("3 - Actualiza un libro\n");
        sb.append("4 - Borra un libro\n");
        sb.append("5 - Salir\n");
        System.out.println(sb.toString());
    }

    private String askForString(Scanner sc, String name){
        System.out.print(name + ": ");
        String nombre = sc.nextLine();
        return nombre;
    }

    private double askForDouble(Scanner sc, String name){
        System.out.print(name + ": ");
        double p;

        try{
            p = sc.nextDouble();
        }
        catch(NoSuchElementException e){
            p = 0.0;
        }
        
        sc.nextLine();
        return p;
    }

    private int askForInt(Scanner sc, String name){
        System.out.print(name + ": ");
        int p;

        try{
            p = sc.nextInt();
        }
        catch(NoSuchElementException e){
            p = 0;
        }
        
        sc.nextLine();
        return p;
    }

    public void añadirLibro(){
        String nombre = askForString(sc, "Nombre");
        String autor = askForString(sc, "Autor");
        String ISBN = askForString(sc, "ISBN");
        double precio = askForDouble(sc, "Precio");

        this.em.getTransaction().begin();
        this.em.persist(new Libro(ISBN, nombre, autor, precio));
        this.em.getTransaction().commit();

        System.out.println("Añadido a la BBDD: " + this.em.find(Libro.class, ISBN));
        

    }

    public void listarLibros(){
        this.em.createQuery("SELECT l FROM Libro l ORDER BY l.titulo", Libro.class)
        .getResultList()
        .forEach(l -> System.out.println(l));
    }

    /**
     * Pregunta por el ISBN del libro a actualizar. Se comprueba que ese codigo pertenece a un libro existente.
     * PReguntamos por los campos nombre, autor y precio. Si el usuario no introduce texto y le da a enter mantenemos
     * la información previa de ese campo. Además, en el nombre comprobaremos que el nuevo nombre no corresponde
     * a ningun libro en la BBDD. Finalmente ejecutamos la actualizacion.
     */
    public void actualizarLibro(){
        Libro l = null;
        String ISBN = null;

        do{
          ISBN = askForString(sc, "ISBN or cancel");  
          if(ISBN.toLowerCase().equals("cancel")) {
            
            return;
          }
          l = em.find(Libro.class, ISBN);
        } while(l == null);

        System.out.println("Nuevos datos:");

        String titulo = askForString(sc, String.format("Nombre (%s)", l.getTitulo()));
        if((long) em.createQuery("SELECT COUNT(*) FROM Libro l WHERE l.titulo == :title")
            .setParameter("title", titulo).getSingleResult() != 0){
            System.err.println("ERROR: No puedes introducir el nombre de un libro existente");
            
            return;
        }
        titulo = (titulo.isEmpty()) ? l.getTitulo() : titulo;

        String autor = askForString(sc, String.format("Autor (%s)", l.getAutor()));
        autor = (autor.isEmpty()) ? l.getAutor() : autor;

        double precio = askForDouble(sc, String.format("Precio (%.2f€)", l.getPrecio()));
        precio = (precio == 0.) ? l.getPrecio() : precio;
        
        em.getTransaction().begin();
        l.setTitulo(titulo);
        l.setAutor(autor);
        l.setPrecio(precio);
        em.getTransaction().commit();

        System.out.println("Libro actualizado: " + em.find(Libro.class, ISBN));
    }

    /**
     * El usuario introduce el ISBN de un libro y. si este existe, es borrado de la BBDD
     */
    public void borrarLibro(){

        String ISBN = askForString(sc, "ISBN");

        Libro l = em.find(Libro.class, ISBN);

        if(l == null) {
            System.out.println("No existe el libro.");
        }
        else{
            em.getTransaction().begin();
            em.remove(l);
            em.getTransaction().commit();

            System.out.printf("Libro eliminado: %s\n", l.toString());
        }
    }

    public void run(){
        int input = 0;
        do{
            printMenu();
            input = askForInt(sc, "Acción");
            switch (input) {
                case 1:
                    añadirLibro();                 
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    actualizarLibro();
                    break;
                case 4:
                    borrarLibro();
                    break;
                default:
                    break;
            }
            

        } while(input != 5);
    }

    public static void main(String[] args) {
        ConsolaLibro con = new ConsolaLibro();
        con.run();
    }

    
}
