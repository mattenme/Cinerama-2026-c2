package cinerama.repository;

import cinerama.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Query("""
            SELECT c FROM Compra c
            JOIN FETCH c.cliente
            JOIN FETCH c.funcion f
            JOIN FETCH f.pelicula
            JOIN FETCH f.sala
            LEFT JOIN FETCH c.asiento
            WHERE c.cliente.idCliente = :idCliente
            ORDER BY c.fechaCompra DESC, c.idCompra DESC
            """)
    List<Compra> findByClienteConRelaciones(@Param("idCliente") Long idCliente);

    @Query("""
            SELECT c FROM Compra c
            JOIN FETCH c.cliente
            JOIN FETCH c.funcion f
            JOIN FETCH f.pelicula
            JOIN FETCH f.sala
            LEFT JOIN FETCH c.asiento
            WHERE c.funcion.idFuncion = :idFuncion
            ORDER BY c.fechaCompra DESC, c.idCompra DESC
            """)
    List<Compra> findByFuncionConRelaciones(@Param("idFuncion") Long idFuncion);

    @Query("""
            SELECT c FROM Compra c
            JOIN FETCH c.cliente
            JOIN FETCH c.funcion f
            JOIN FETCH f.pelicula
            JOIN FETCH f.sala
            LEFT JOIN FETCH c.asiento
            WHERE c.funcion.idFuncion = :idFuncion AND UPPER(c.estado) IN ('COMPLETADA', 'CONFIRMADA')
            ORDER BY c.fechaCompra DESC, c.idCompra DESC
            """)
    List<Compra> findConfirmedByFuncion(@Param("idFuncion") Long idFuncion);

    @Query("""
            SELECT c FROM Compra c
            JOIN FETCH c.cliente
            JOIN FETCH c.funcion f
            JOIN FETCH f.pelicula
            JOIN FETCH f.sala
            LEFT JOIN FETCH c.asiento
            ORDER BY c.fechaCompra DESC, c.idCompra DESC
            """)
    List<Compra> findAllConRelaciones();

    @Query("""
            SELECT c FROM Compra c
            JOIN FETCH c.cliente
            JOIN FETCH c.funcion f
            JOIN FETCH f.pelicula
            JOIN FETCH f.sala
            LEFT JOIN FETCH c.asiento
            WHERE c.idCompra = :id
            """)
    Optional<Compra> findByIdConRelaciones(@Param("id") Long id);

    boolean existsByFuncion_IdFuncionAndAsiento_IdAsientoAndEstadoIn(
            Long idFuncion, Long idAsiento, List<String> estados);

    /** Una compra de varias butacas comparte código QR: se toma la primera fila. */
    Optional<Compra> findFirstByCodigoQrOrderByIdCompraAsc(String codigoQr);
}
