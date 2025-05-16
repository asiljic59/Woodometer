    package com.example.woodometer.viewmodels

    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import androidx.sqlite.SQLITE_DATA_TEXT
    import com.example.woodometer.Woodometer
    import com.example.woodometer.model.Biodiverzitet
    import com.example.woodometer.model.Krug
    import com.example.woodometer.model.MrtvoStablo
    import com.example.woodometer.model.Stablo
    import com.example.woodometer.dao.StabloDao
    import com.example.woodometer.repository.BiodiverzitetRepository
    import com.example.woodometer.repository.KrugRepository
    import com.example.woodometer.repository.MrtvoStabloRepository
    import com.example.woodometer.repository.StabloRepository
    import com.example.woodometer.utils.PreferencesUtils
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.debounce
    import kotlinx.coroutines.flow.distinctUntilChanged
    import kotlinx.coroutines.flow.launchIn
    import kotlinx.coroutines.flow.onEach
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import java.util.UUID

    class KrugViewModel : ViewModel() {
        private val stabloRepository: StabloRepository = StabloRepository()
        private val krugRepository : KrugRepository = KrugRepository()
        private val biodiverzitetRepository : BiodiverzitetRepository = BiodiverzitetRepository()
        private val mrtvoStabloRepository : MrtvoStabloRepository = MrtvoStabloRepository()

        // ovo koristimo za edit/preview kruga
        private val _trenutniKrug = MutableLiveData<Krug>().apply {
            value = Krug()
        }
        //ovo ce nam samo sluzitit da znamo da li se neki krug radi i da li treba da se zavrsi
        private val _radniKrug = MutableLiveData<Krug?>().apply {
            value = null
        }
        private val _trenutnoStablo = MutableLiveData<Stablo>().apply {
            value = Stablo ()
        }
        private val _stablaKruga = MutableLiveData<MutableList<Stablo>>().apply {
            value = mutableListOf()
        }


        private val _trenutnaMrtvaStabla = MutableLiveData<MutableList<MrtvoStablo>>(mutableListOf())

        private val _trenutnoMrtvoStablo = MutableLiveData<MrtvoStablo>().apply {
            value = MrtvoStablo(rbr = 1,krugId = _trenutniKrug.value?.id!!)
        }
        private val _biodiverzitet = MutableLiveData<Biodiverzitet>().apply {
            value = Biodiverzitet(_trenutniKrug.value?.id!!)
        }

        val trenutniKrug: LiveData<Krug> get() = _trenutniKrug
        val radniKrug : LiveData<Krug?> get() = _radniKrug
        val trenutnaMrtvaStabla: LiveData<MutableList<MrtvoStablo>> = _trenutnaMrtvaStabla
        val trenutnoStablo: LiveData<Stablo> get() = _trenutnoStablo
        val stablaKruga : LiveData<MutableList<Stablo>> get() = _stablaKruga
        val mrtvoStablo : LiveData<MrtvoStablo> get() = _trenutnoMrtvoStablo
        val biodiverzitet : LiveData<Biodiverzitet> get() = _biodiverzitet

        private var poslednjaVrsta = 61

        suspend fun updateTrenutnoStablo() {
            withContext(Dispatchers.IO){
                _trenutnoStablo.value?.let {
                    if (!it.isDefault()){
                        stabloRepository.upsert(it)
                    }
                }
            }
            updateStablaKruga()
        }

        fun setTrenutnoStablo(stablo: Stablo) {
            _trenutnoStablo.value = stablo
        }


        fun resetStablo() {
            val vrsta = trenutnoStablo.value?.vrsta!!
            val stablaList = _stablaKruga.value ?: emptyList()
            val rbr = stablaList.maxOfOrNull { it.rbr } ?: 0
            _trenutnoStablo.value  = _trenutniKrug.value?.id?.let { Stablo(rbr = rbr+1, krugId = it, vrsta = vrsta) }
        }
        suspend fun deleteStablo(rbr: Int) {
            withContext(Dispatchers.IO) {
                val stablo = stablaKruga.value?.find { it.rbr == rbr }
                stabloRepository.delete(stablo!!)

                val updatedList = stablaKruga.value?.toMutableList()
                updatedList?.removeAll { it.rbr == rbr }

                withContext(Dispatchers.Main) {
                    _stablaKruga.value = updatedList
                }
            }
        }

        ///MRTVA STABLA !!!!!!!!!!!!!
        fun setTrenutnaMrtvaStabla(stabla: MutableList<MrtvoStablo>) {
            _trenutnaMrtvaStabla.value = stabla
        }
        //SETOVANJE NOVOG MRTVOG STABLA KOJE CE DA BUDE AZURIRANO
        fun setMrtvoStabloToEdit(item: MrtvoStablo) {
            _trenutnoMrtvoStablo.value = item // Important to copy to avoid reference issues
        }

        fun initNewMrtvoStablo() {
            val lista = trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
            val maxRbr = lista.maxOfOrNull { it.rbr } ?: 0
            _trenutnoMrtvoStablo.value = MrtvoStablo(
                rbr = maxRbr + 1,
                krugId = trenutniKrug.value?.id ?: return,
                vrsta = poslednjaVrsta
            )
        }

        //DODAVANJE NOVOG MRTVOG STABLA
        suspend fun addMrtvoStablo() {
            val newStablo = mrtvoStablo.value ?: return

            poslednjaVrsta = newStablo.vrsta

            withContext(Dispatchers.IO) {
                mrtvoStabloRepository.upsert(newStablo)
            }
            val updatedList = _trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
            updatedList.add(newStablo)
            _trenutnaMrtvaStabla.value = updatedList


        }

        //BRISANJE MRTVOG STABLA
        fun deleteMrtvoStablo(rbr : Int){
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    val stablo = trenutnaMrtvaStabla.value?.find { it.rbr == rbr }
                    mrtvoStabloRepository.delete(stablo!!)
                    val updatedList = trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
                    updatedList.removeAll{it.rbr == rbr}
                    _trenutnaMrtvaStabla.postValue(updatedList)
                }
            }
        }

        fun editMrtvoStablo() {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    mrtvoStablo.value?.let { newStablo ->
                        mrtvoStabloRepository.upsert(newStablo)
                        val updatedList = _trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
                        // Racunamo novi redni broj tako sto uzimamo maximum iz liste +1
                        val existingIndex = updatedList.indexOfFirst { it.id == newStablo.id }
                        if (existingIndex != -1) {updatedList[existingIndex] = newStablo}
                        val maxRbr = updatedList.maxOfOrNull { it.rbr } ?: 0  // If list is empty, start from 1
                        _trenutnaMrtvaStabla.postValue(updatedList)
                        // Update the _trenutnoMrtvoStablo LiveData
                        _trenutnoMrtvoStablo.postValue(MrtvoStablo(rbr = maxRbr + 1,krugId = trenutniKrug.value?.id!!))
                    }
                }
            }
        }
        fun getMrtvaStablaByKrug() {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    val mrtvaStabla = trenutniKrug.value?.let { mrtvoStabloRepository.getByKrug(it.id) }!!
                    if (mrtvaStabla.isNotEmpty()){
                        _trenutnaMrtvaStabla.postValue(mrtvaStabla)
                    }
                    val maxRbr = mrtvaStabla.maxOfOrNull { it.rbr } ?: 0
                    _trenutnoMrtvoStablo.postValue(MrtvoStablo(krugId = trenutniKrug.value?.id!!,rbr = maxRbr + 1 ))
                }
            }
        }
        fun updateMrtvaStabla() {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    val stabla  = trenutniKrug.value?.let { mrtvoStabloRepository.getByKrug(it.id) }
                    _trenutnaMrtvaStabla.postValue(stabla!!)
                }
            }
        }
        ////MRTVA STABLA !!!! CRUD

        suspend fun resetKrug(dokumentId : UUID) {
            _trenutniKrug.value?.dokumentId = dokumentId
            _radniKrug.value = _trenutniKrug.value
            addKrug()
            _stablaKruga.value = mutableListOf()
            _trenutnoStablo.value = Stablo(krugId = _trenutniKrug.value?.id!!)
            _trenutnoMrtvoStablo.value = MrtvoStablo(krugId = _trenutniKrug.value?.id!!)
            _trenutnaMrtvaStabla.value = mutableListOf()
            _biodiverzitet.value = Biodiverzitet(krugId = _trenutniKrug.value?.id!!)

        }

        fun addKrug() {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    _trenutniKrug.value?.let { krugRepository.add(it) }
                }
            }
        }

        fun deleteKrug() {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    _trenutniKrug.value?.let { krugRepository.delete(it) }
                }
            }
        }

        fun setTrenutniKrug(krug : Krug) {
            _trenutniKrug.value = krug
        }

        fun setRadniKrug(id : UUID){
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    _radniKrug.postValue(krugRepository.getById(id))
                }
            }
        }

        fun setDefaultRadniKrug(){
            _radniKrug.postValue(null)
        }

        suspend fun setStablaKruga() {
            val stabla = withContext(Dispatchers.IO) {
                trenutniKrug.value?.let { stabloRepository.getByKrug(it.id) }
            }
            if (!stabla.isNullOrEmpty()) {
                _trenutnoStablo.value = stabla[0]
            }
            _stablaKruga.value = stabla

        }
        //dodavanje novog stabla u stabla kruga
        fun updateStablaKruga(){
            _trenutnoStablo.value?.let {
                if (_stablaKruga.value?.none { it.id == _trenutnoStablo.value?.id } == true) {
                    val updatedList = _stablaKruga.value?.toMutableList() ?: mutableListOf()
                    updatedList.add(it)
                    _stablaKruga.value = updatedList
                }
            }
        }


        //BIODIVERZITET CRUD
        fun updateBiodiverzitet() {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    biodiverzitet.value?.let { biodiverzitetRepository.upsert(it) }
                }
            }
        }

        fun getBiodiverzitetByKrug() {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    val biodiverzitet = trenutniKrug.value?.let { biodiverzitetRepository.getByKrug(it.id) }
                    if (biodiverzitet == null){
                        _biodiverzitet.postValue(Biodiverzitet(krugId = trenutniKrug.value?.id!!))
                    }else{
                        _biodiverzitet.postValue(biodiverzitet)
                    }

                }
            }
        }

        suspend fun isKrugValid(): Triple<Boolean, List<Int>,List<Int>> {
            val radniKrugStabla = withContext(Dispatchers.IO) {
                radniKrug.value?.let { stabloRepository.getByKrug(it.id) } ?: emptyList()
            }
            val radniKrugMrtvaStabla = withContext(Dispatchers.IO){
                radniKrug.value?.let {mrtvoStabloRepository.getByKrug(it.id)} ?: emptyList()
            }
            val invalidStabla = areStablaValid(radniKrugStabla)
            val invalidMrtvaStabla = areMrtvaStablaValid(radniKrugMrtvaStabla.toMutableList())
            return Triple(invalidStabla.isEmpty(), invalidStabla,invalidMrtvaStabla)
        }

        //provera da li su SVA stabla kruga validna!
        fun areStablaValid(stabla: List<Stablo>) : List<Int>{
            val invalidStabla : MutableList<Int> = mutableListOf()

            stabla.forEach{stablo ->
                if (stablo.hasAnyDefaultVal(radniKrug.value?.permanentna!!)){
                    invalidStabla.add(stablo.rbr)
                }
            }
            return invalidStabla
        }
        fun areMrtvaStablaValid(stabla: MutableList<MrtvoStablo>): List<Int> {
            val invalidMrtvaStabla : MutableList<Int> = mutableListOf()
            stabla.forEach{stablo ->
                if (stablo.hasAnyDefaultVal()){
                    invalidMrtvaStabla.add(stablo.rbr)
                }
            }
            return invalidMrtvaStabla
        }

        fun setDefaultStablo() {
            val firstStablo = _stablaKruga.value?.firstOrNull()
                ?: Stablo(krugId = trenutniKrug.value?.id!!)

            _trenutnoStablo.value = firstStablo
        }
        fun getStabloHash() : Int{
            return _trenutnoStablo.value?.hashCode()!!
        }

        fun getStabloIndex(stablo: Stablo): Int {
            return stablaKruga.value?.indexOf(stablo)!!
        }

        fun isRadniKrug(): Boolean {
            return trenutniKrug.value?.id == radniKrug.value?.id
        }




    }
