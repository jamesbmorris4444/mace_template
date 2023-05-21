package com.mace.mace_template.utils

import com.mace.mace_template.repository.storage.Donor

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

    fun donorComparisonByString(donor: Donor): String {
        return donor.lastName + "," + donor.firstName + "," + donor.middleName + "," + donor.dob
    }

//    fun newPatternOfSubpatterns(patternOfSubpatterns: String, index: Int, newPattern: String): String {
//        // patternOfSubpatterns = P|P|P|...|P
//        // if there are N subpatterns then index = 0 to N-1
//        // example of usage: newPatternOfSubpatterns("aaaa|bbbb|cccc|dddd", 2, "xxxxxxxx")
//        // will return the string value: "aaaa|bbbb|xxxxxxxx|dddd"
//        val split: MutableList<String> = patternOfSubpatterns.split('|').toMutableList()
//        val stringBuilder = StringBuilder()
//        split[index] = newPattern
//        for (newIndex in split.indices) {
//            stringBuilder.append(split[newIndex])
//            if (newIndex < split.size - 1) {
//                stringBuilder.append('|')
//            }
//        }
//        return stringBuilder.toString()
//    }
//
//    fun getPatternOfSubpatterns(patternOfSubpatterns: String, index: Int): String {
//        // patternOfSubpatterns = P|P|P|...|P|
//        // if there are N subpatterns then index = 0 to N-1
//        // example of usage: getPatternOfSubpatterns("aaaa|bbbb|cccc|dddd", 2)
//        // will return the string value: "cccc"
//        val split: MutableList<String> = patternOfSubpatterns.split('|').toMutableList()
//        return split[index]
//    }
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