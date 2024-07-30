package pocketDock.com.pocketDock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pocketDock.com.pocketDock.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {
}
