package hys.hmonkeyys.stopsmoking.screen.registration

sealed class RegistrationState {
    object Initialized : RegistrationState()

    data class StoredValue(
        val dateArray: List<String>,
        val nickName: String,
        val amountOfSmoking: Int,
        val tobaccoPrice: Int,
//        val myResolution: String,
    ) : RegistrationState()

    data class CheckNickName(
        val hasNickName: Boolean,
    ) : RegistrationState()

    object EditInformation : RegistrationState()
}
