package com.csi.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.csi.services.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csi.utils.entities.Product;
import com.csi.utils.entities.Seller;
import com.csi.models.ProductDTO;
import com.csi.models.ProductPagedResponseDTO;
import com.csi.models.ProductResponseDTO;
import com.csi.models.Response;
import com.csi.services.ProductService;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductController {
	
	@Autowired ProductService productService;
	@Autowired
    SellerService sellerService;
	
	@PostMapping
	public ResponseEntity<?> saveProduct(ProductDTO dto) {
		System.out.println(dto);
		Product product=ProductDTO.toEntity(dto);
		Seller seller=sellerService.findById(dto.getSellerId());
		product.setSeller(seller);
		productService.addProduct(product,dto.getPic());
		return Response.success(product);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> updateProduct(@RequestBody Product product,@PathVariable("id") int id) {
		System.out.println(product);		
		productService.updateProduct(product);
		return Response.success(product);		
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> findProduct(@PathVariable("id")int id) {
		Product product=productService.findProductById(id);
		return Response.success(ProductResponseDTO.fromEntity(product));
	}
	
	@GetMapping
	public ResponseEntity<?> findAllProducts(Optional<Integer> sellerid) {
		List<ProductResponseDTO> result = new ArrayList<ProductResponseDTO>();
		if(sellerid.isPresent()) {
			System.out.println(sellerid);
			for(Product p : productService.findProducts(sellerid.get())) {
				result.add(ProductResponseDTO.fromEntity(p));
			}
			
		}else {
			for(Product p : productService.allProducts()) {
				result.add(ProductResponseDTO.fromEntity(p));
			}
		}
		
		return Response.success(result);
	}
	
	@GetMapping("/paginated")
	public ResponseEntity<?> paginatedProducts(int page,int pagesize) {
		List<ProductResponseDTO> result = new ArrayList<ProductResponseDTO>();
		Page<Product> data=productService.allProductsPaginated(page, pagesize);
		data.forEach(item-> {
			result.add(ProductResponseDTO.fromEntity(item));
		});
		ProductPagedResponseDTO resp=new ProductPagedResponseDTO();
		resp.setPagesize(pagesize);
		resp.setCurrent(page);
		resp.setTotal(data.getTotalElements());
		resp.setPlist(result);
		return Response.success(resp);
	}
	
	@GetMapping("cats")
	public ResponseEntity<?> findCategoryProducts(String cat,String subcat) {
		List<ProductResponseDTO> result = new ArrayList<ProductResponseDTO>();
		
		for(Product p : productService.categoryProducts(cat, subcat)) {
			result.add(ProductResponseDTO.fromEntity(p));
		}
		
		return Response.success(result);
	}
		
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable("id") int id) {
		productService.deleteProduct(id);
		return Response.status(HttpStatus.OK);
	}


	@GetMapping("/filterbyprice/{price}")
	public ResponseEntity<?> getProductByPrice(@PathVariable int price ){
		List<ProductResponseDTO> result = new ArrayList<ProductResponseDTO>();
		for(Product p : productService.allProducts()) {
			result.add(ProductResponseDTO.fromEntity(p));

		}
		return Response.success(result.stream().filter(p-> p.getPrice()<=5000));
	}

	@GetMapping("/sortproductbyprice")
	public ResponseEntity<?> sortProductByPrice(){

		return Response.success(productService.allProducts().stream().sorted(Comparator.comparingLong(Product::getPrice)).collect(Collectors.toList()));
	}

	@GetMapping("/sortdatabyproductname")
	public ResponseEntity<?> sortDataByProductName(){
		return Response.success(productService.allProducts().stream().sorted((p1, p2)-> p1.getPname().compareTo(p2.getPname())).collect(Collectors.toList()));
	}
}
