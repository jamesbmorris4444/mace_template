package com.mace.mace_template.repository.storage

import androidx.room.*
import io.reactivex.Single

@Dao
interface DBDao {

    // insertions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDonor(donor: Donor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalDonors(donors: List<Donor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<Product>)

    @Transaction
    fun insertDonorsAndProductLists(donors: List<Donor>, products: List<List<Product>>) {
        insertLocalDonors(donors)
        for (index in products.indices) {
            insertProducts(products[index])
        }
    }

    @Transaction
    fun insertDonorAndProducts(donor: Donor, products: List<Product>) {
        insertDonor(donor)
        insertProducts(products)
    }

    // queries

    @Query("SELECT COUNT(id) FROM donors")
    fun getDonorEntryCount(): Int

    @Query("SELECT COUNT(id) FROM products")
    fun getProductEntryCount(): Single<Int>

    @Query("SELECT * FROM donors WHERE title LIKE :searchLast AND poster_path LIKE :searchFirst")
    fun donorsFromFullName(searchLast: String, searchFirst :String) : List<Donor>

    @Query("SELECT * FROM donors WHERE title LIKE :searchLast AND release_date LIKE :dob")
    fun donorsFromFullNameWithProducts(searchLast: String, dob: String) : List<DonorWithProducts>

    @Query("SELECT * FROM donors WHERE title = :searchLast AND release_date = :searchDate")
    fun donorFromNameAndDateWithProducts(searchLast: String, searchDate: String) : DonorWithProducts

    // get all donors and products

    @Query("SELECT * from donors")
    fun loadAllDonorsWithProducts(): List<DonorWithProducts>

    @Query("SELECT * FROM products")
    fun getAllProducts(): List<Product>

    @Query("SELECT * FROM donors")
    fun getAllDonors(): List<Donor>


}