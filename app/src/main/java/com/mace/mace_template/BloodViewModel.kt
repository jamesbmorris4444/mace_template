package com.mace.mace_template

import android.app.Application
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import com.mace.mace_template.repository.DatabaseSelector
import com.mace.mace_template.repository.RepositoryImpl
import com.mace.mace_template.repository.storage.Donor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class BloodViewModel(private val app: Application) : AndroidViewModel(app), KoinComponent {

    private val repository : RepositoryImpl by inject()

    @Composable
    fun RefreshRepository(refreshCompleted: () -> Unit) {
        repository.refreshDatabase(app.applicationContext, refreshCompleted)
    }

    fun handleSearchClick(searchKey: String, searchCompleted: (List<Donor>) -> Unit) {
        repository.handleSearchClick(searchKey, searchCompleted)
    }

    fun insertDonorIntoDatabase(donor: Donor, completed: (Boolean) -> Unit) {
        repository.insertDonorIntoDatabase(DatabaseSelector.STAGING_DB, donor, completed)
    }

    fun getResources(): Resources {
        return app.resources
    }

    fun setBloodDatabase() {
        repository.setBloodDatabase(app.applicationContext)
    }

//    fun onUpdateClicked(donor: Donor) {
//        // update new values into donor
//
//        // change last name
//        editTextDisplayModifyLastName.get()?.let { editTextDisplayModifyLastName ->
//            donor.lastName = editTextDisplayModifyLastName
//        }
//
//        // change first name
//        editTextDisplayModifyFirstName.get()?.let { editTextDisplayModifyFirstName ->
//            donor.firstName = editTextDisplayModifyFirstName
//        }
//
//        // change middle name
//        editTextDisplayModifyMiddleName.get()?.let { editTextDisplayModifyMiddleName ->
//            donor.middleName = editTextDisplayModifyMiddleName
//        }
//
//        // change date of birth
//        editTextDisplayModifyDob.get()?.let { editTextDisplayModifyDob ->
//            donor.dob = editTextDisplayModifyDob
//        }
//
//        // change gender
//        callbacks.fetchRadioButton(R.id.radio_male)?.let {
//            donor.gender = it.isChecked
//        }
//
//        // change ABO/Rh
//        donor.aboRh = currentAboRhSelectedValue
//
//        // change branch
//        donor.branch = currentMilitaryBranchSelectedValue
//
//        val atLeastOneEntryChanged =
//            donor.lastName != originalLastName ||
//                    donor.firstName != originalFirstName ||
//                    donor.middleName != originalMiddleName ||
//                    donor.dob != originalDob ||
//                    donor.gender != originalGender ||
//                    donor.aboRh != originalAboRh ||
//                    donor.branch != originalBranch
//
//        if (atLeastOneEntryChanged && isDonorValid(donor)) {
//            repository.insertDonorIntoDatabase(repository.stagingBloodDatabase, donor, transitionToCreateDonation, this::showStagingDatabaseEntries)
//            if (repository.newDonorInProgress) {
//                repository.newDonor = donor
//                if (transitionToCreateDonation) {
//                    // retrieve the new donor from the staging database in order to set its id
//                    repository.retrieveDonorFromNameAndDob(
//                        callbacks.fetchActivity().fetchRootView().findViewById(R.id.main_progress_bar),
//                        donor,
//                        this::completeProcessingOfNewDonor)
//                }
//            }
//        } else {
//
//        }
//        repository.newDonorInProgress = false
//    }

}