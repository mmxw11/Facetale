package wepa.ftale.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import wepa.ftale.domain.Image;

/**
 * @author Matias
 */
public interface ImageRepository extends JpaRepository<Image, UUID> {
}