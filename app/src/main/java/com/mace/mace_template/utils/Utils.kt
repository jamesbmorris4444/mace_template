package com.mace.mace_template.utils

import com.mace.mace_template.logger.LogUtils
import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.DonorWithProducts
import com.mace.mace_template.utils.Constants.LOG_TAG

object Utils {
//
//    fun showKeyboard(view: View?) {
//        if (view == null) return
//        val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
//    }
//
//    fun donorEquals(donor: Donor, otherDonor: Donor): Boolean {
//        return donor.lastName == otherDonor.lastName && donor.firstName == otherDonor.firstName && donor.middleName == otherDonor.middleName && donor.dob == otherDonor.dob
//    }

    fun donorComparisonByString(donorWithProducts: Donor): String {
        return "${donorWithProducts.lastName},${donorWithProducts.dob}"
    }

    fun donorComparisonByStringWithProducts(donorWithProducts: DonorWithProducts): String {
        return "${donorWithProducts.donor.lastName},${donorWithProducts.donor.dob}"
    }

    fun donorBloodTypeComparisonByString(donorWithProducts: Donor): String {
        return donorWithProducts.aboRh
    }

    fun donorLastNameComparisonByString(donorWithProducts: Donor): String {
        return donorWithProducts.lastName
    }

    fun prettyPrintList(list: List<DonorWithProducts>) {
        LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "=======================")
        list.forEach {
            LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "resultListElement=$it")
        }
        LogUtils.D(LOG_TAG, LogUtils.FilterTags.withTags(LogUtils.TagFilter.TMP), "=======================")
    }
//
//    fun donorsAndProductsList(listOfDonorsAndProducts: List<DonorWithProducts>)  {
//        for (item in listOfDonorsAndProducts) {
//            LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.THM), String.format("donor=%s", item.donor.lastName))
//            for (product in item.products) {
//                LogUtils.D("JIMX", LogUtils.FilterTags.withTags(LogUtils.TagFilter.THM), String.format("  product=%s---%s", product.din, product.aboRh))
//            }
//        }
//    }
}