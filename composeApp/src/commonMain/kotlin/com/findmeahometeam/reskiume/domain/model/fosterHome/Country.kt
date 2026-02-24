package com.findmeahometeam.reskiume.domain.model.fosterHome

import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.france
import reskiume.composeapp.generated.resources.portugal
import reskiume.composeapp.generated.resources.spain
import reskiume.composeapp.generated.resources.united_kingdom
import reskiume.composeapp.generated.resources.unselected_country

enum class Country(val countryName: String) {
    UNSELECTED("unselected"),
    /*AFGHANISTAN("afghanistan"),
    ALBANIA("albania"),
    ALGERIA("algeria"),
    ANDORRA("andorra"),
    ANGOLA("angola"),
    ANTIGUA_AND_BARBUDA("antigua_and_barbuda"),
    ARGENTINA("argentina"),
    ARMENIA("armenia"),
    AUSTRALIA("australia"),
    AUSTRIA("austria"),
    AZERBAIJAN("azerbaijan"),
    BAHAMAS("bahamas"),
    BAHRAIN("bahrain"),
    BANGLADESH("bangladesh"),
    BARBADOS("barbados"),
    BELARUS("belarus"),
    BELGIUM("belgium"),
    BELIZE("belize"),
    BENIN("benin"),
    BHUTAN("bhutan"),
    BOLIVIA("bolivia"),
    BOSNIA_AND_HERZEGOVINA("bosnia_and_herzegovina"),
    BOTSWANA("botswana"),
    BRAZIL("brazil"),
    BRUNEI("brunei"),
    BULGARIA("bulgaria"),
    BURKINA_FASO("burkina_faso"),
    BURUNDI("burundi"),
    CAMBODIA("cambodia"),
    CAMEROON("cameroon"),
    CANADA("canada"),
    CAPE_VERDE("cape_verde"),
    CENTRAL_AFRICAN_REPUBLIC("central_african_republic"),
    CHAD("chad"),
    CHILE("chile"),
    CHINA("china"),
    COLOMBIA("colombia"),
    COMOROS("comoros"),
    CONGO("congo"),
    COSTA_RICA("costa_rica"),
    CROATIA("croatia"),
    CUBA("cuba"),
    CYPRUS("cyprus"),
    CZECH_REPUBLIC("czech_republic"),
    DENMARK("denmark"),
    DJIBOUTI("djibouti"),
    DOMINICA("dominica"),
    DOMINICAN_REPUBLIC("dominican_republic"),
    ECUADOR("ecuador"),
    EGYPT("egypt"),
    EL_SALVADOR("el_salvador"),
    EQUATORIAL_GUINEA("equatorial_guinea"),
    ERITREA("eritrea"),
    ESTONIA("estonia"),
    ESWATINI("eswatini"),
    ETHIOPIA("ethiopia"),
    FIJI("fiji"),
    FINLAND("finland"),*/
    FRANCE("france"),
    /*GABON("gabon"),
    GAMBIA("gambia"),
    GEORGIA("georgia"),
    GERMANY("germany"),
    GHANA("ghana"),
    GREECE("greece"),
    GRENADA("grenada"),
    GUATEMALA("guatemala"),
    GUINEA("guinea"),
    GUINEA_BISSAU("guinea_bissau"),
    GUYANA("guyana"),
    HAITI("haiti"),
    HONDURAS("honduras"),
    HUNGARY("hungary"),
    ICELAND("iceland"),
    INDIA("india"),
    INDONESIA("indonesia"),
    IRAN("iran"),
    IRAQ("iraq"),
    IRELAND("ireland"),
    ISRAEL("israel"),
    ITALY("italy"),
    IVORY_COAST("ivory_coast"),
    JAMAICA("jamaica"),
    JAPAN("japan"),
    JORDAN("jordan"),
    KAZAKHSTAN("kazakhstan"),
    KENYA("kenya"),
    KIRIBATI("kiribati"),
    KUWAIT("kuwait"),
    KYRGYZSTAN("kyrgyzstan"),
    LAOS("laos"),
    LATVIA("latvia"),
    LEBANON("lebanon"),
    LESOTHO("lesotho"),
    LIBERIA("liberia"),
    LIBYA("libya"),
    LIECHTENSTEIN("liechtenstein"),
    LITHUANIA("lithuania"),
    LUXEMBOURG("luxembourg"),
    MADAGASCAR("madagascar"),
    MALAWI("malawi"),
    MALAYSIA("malaysia"),
    MALDIVES("maldives"),
    MALI("mali"),
    MALTA("malta"),
    MARSHALL_ISLANDS("marshall_islands"),
    MAURITANIA("mauritania"),
    MAURITIUS("mauritius"),
    MEXICO("mexico"),
    MICRONESIA("micronesia"),
    MOLDOVA("moldova"),
    MONACO("monaco"),
    MONGOLIA("mongolia"),
    MONTENEGRO("montenegro"),
    MOROCCO("morocco"),
    MOZAMBIQUE("mozambique"),
    MYANMAR("myanmar"),
    NAMIBIA("namibia"),
    NAURU("nauru"),
    NEPAL("nepal"),
    NETHERLANDS("netherlands"),
    NEW_ZEALAND("new_zealand"),
    NICARAGUA("nicaragua"),
    NIGER("niger"),
    NIGERIA("nigeria"),
    NORTH_KOREA("north_korea"),
    NORTH_MACEDONIA("north_macedonia"),
    NORWAY("norway"),
    OMAN("oman"),
    PAKISTAN("pakistan"),
    PALAU("palau"),
    PANAMA("panama"),
    PAPUA_NEW_GUINEA("papua_new_guinea"),
    PARAGUAY("paraguay"),
    PERU("peru"),
    PHILIPPINES("philippines"),
    POLAND("poland"),*/
    PORTUGAL("portugal"),
    /*QATAR("qatar"),
    ROMANIA("romania"),
    RUSSIA("russia"),
    RWANDA("rwanda"),
    SAINT_KITTS_AND_NEVIS("saint_kitts_and_nevis"),
    SAINT_LUCIA("saint_lucia"),
    SAINT_VINCENT_AND_THE_GRENADINES("saint_vincent_and_the_grenadines"),
    SAMOA("samoa"),
    SAN_MARINO("san_marino"),
    SAO_TOME_AND_PRINCIPE("sao_tome_and_principe"),
    SAUDI_ARABIA("saudi_arabia"),
    SENEGAL("senegal"),
    SERBIA("serbia"),
    SEYCHELLES("seychelles"),
    SIERRA_LEONE("sierra_leone"),
    SINGAPORE("singapore"),
    SLOVAKIA("slovakia"),
    SLOVENIA("slovenia"),
    SOLOMON_ISLANDS("solomon_islands"),
    SOMALIA("somalia"),
    SOUTH_AFRICA("south_africa"),
    SOUTH_KOREA("south_korea"),
    SOUTH_SUDAN("south_sudan"),*/
    SPAIN("spain"),
    /*SRI_LANKA("sri_lanka"),
    SUDAN("sudan"),
    SURINAME("suriname"),
    SWEDEN("sweden"),
    SWITZERLAND("switzerland"),
    SYRIA("syria"),
    TAJIKISTAN("tajikistan"),
    TANZANIA("tanzania"),
    THAILAND("thailand"),
    TIMOR_LESTE("timor_leste"),
    TOGO("togo"),
    TONGA("tonga"),
    TRINIDAD_AND_TOBAGO("trinidad_and_tobago"),
    TUNISIA("tunisia"),
    TURKEY("turkey"),
    TURKMENISTAN("turkmenistan"),
    TUVALU("tuvalu"),
    UGANDA("uganda"),
    UKRAINE("ukraine"),
    UNITED_ARAB_EMIRATES("united_arab_emirates"),*/
    UNITED_KINGDOM("united_kingdom"),
    /*UNITED_STATES("united_states"),
    URUGUAY("uruguay"),
    UZBEKISTAN("uzbekistan"),
    VANUATU("vanuatu"),
    VATICAN_CITY("vatican_city"),
    VENEZUELA("venezuela"),
    VIETNAM("vietnam"),
    YEMEN("yemen"),
    ZAMBIA("zambia"),
    ZIMBABWE("zimbabwe")*/
}

fun Country.toStringResource(): StringResource {
    return when(this) {
        Country.UNSELECTED -> Res.string.unselected_country

        /*Country.AFGHANISTAN -> Res.string.afghanistan
        Country.ALBANIA -> Res.string.albania
        Country.ALGERIA -> Res.string.algeria
        Country.ANDORRA -> Res.string.andorra
        Country.ANGOLA -> Res.string.angola
        Country.ANTIGUA_AND_BARBUDA -> Res.string.antigua_and_barbuda
        Country.ARGENTINA -> Res.string.argentina
        Country.ARMENIA -> Res.string.armenia
        Country.AUSTRALIA -> Res.string.australia
        Country.AUSTRIA -> Res.string.austria
        Country.AZERBAIJAN -> Res.string.azerbaijan
        Country.BAHAMAS -> Res.string.bahamas
        Country.BAHRAIN -> Res.string.bahrain
        Country.BANGLADESH -> Res.string.bangladesh
        Country.BARBADOS -> Res.string.barbados
        Country.BELARUS -> Res.string.belarus
        Country.BELGIUM -> Res.string.belgium
        Country.BELIZE -> Res.string.belize
        Country.BENIN -> Res.string.benin
        Country.BHUTAN -> Res.string.bhutan
        Country.BOLIVIA -> Res.string.bolivia
        Country.BOSNIA_AND_HERZEGOVINA -> Res.string.bosnia_and_herzegovina
        Country.BOTSWANA -> Res.string.botswana
        Country.BRAZIL -> Res.string.brazil
        Country.BRUNEI -> Res.string.brunei
        Country.BULGARIA -> Res.string.bulgaria
        Country.BURKINA_FASO -> Res.string.burkina_faso
        Country.BURUNDI -> Res.string.burundi
        Country.CAMBODIA -> Res.string.cambodia
        Country.CAMEROON -> Res.string.cameroon
        Country.CANADA -> Res.string.canada
        Country.CAPE_VERDE -> Res.string.cape_verde
        Country.CENTRAL_AFRICAN_REPUBLIC -> Res.string.central_african_republic
        Country.CHAD -> Res.string.chad
        Country.CHILE -> Res.string.chile
        Country.CHINA -> Res.string.china
        Country.COLOMBIA -> Res.string.colombia
        Country.COMOROS -> Res.string.comoros
        Country.CONGO -> Res.string.congo
        Country.COSTA_RICA -> Res.string.costa_rica
        Country.CROATIA -> Res.string.croatia
        Country.CUBA -> Res.string.cuba
        Country.CYPRUS -> Res.string.cyprus
        Country.CZECH_REPUBLIC -> Res.string.czech_republic
        Country.DENMARK -> Res.string.denmark
        Country.DJIBOUTI -> Res.string.djibouti
        Country.DOMINICA -> Res.string.dominica
        Country.DOMINICAN_REPUBLIC -> Res.string.dominican_republic
        Country.ECUADOR -> Res.string.ecuador
        Country.EGYPT -> Res.string.egypt
        Country.EL_SALVADOR -> Res.string.el_salvador
        Country.EQUATORIAL_GUINEA -> Res.string.equatorial_guinea
        Country.ERITREA -> Res.string.eritrea
        Country.ESTONIA -> Res.string.estonia
        Country.ESWATINI -> Res.string.eswatini
        Country.ETHIOPIA -> Res.string.ethiopia
        Country.FIJI -> Res.string.fiji
        Country.FINLAND -> Res.string.finland*/
        Country.FRANCE -> Res.string.france
        /*Country.GABON -> Res.string.gabon
        Country.GAMBIA -> Res.string.gambia
        Country.GEORGIA -> Res.string.georgia
        Country.GERMANY -> Res.string.germany
        Country.GHANA -> Res.string.ghana
        Country.GREECE -> Res.string.greece
        Country.GRENADA -> Res.string.grenada
        Country.GUATEMALA -> Res.string.guatemala
        Country.GUINEA -> Res.string.guinea
        Country.GUINEA_BISSAU -> Res.string.guinea_bissau
        Country.GUYANA -> Res.string.guyana
        Country.HAITI -> Res.string.haiti
        Country.HONDURAS -> Res.string.honduras
        Country.HUNGARY -> Res.string.hungary
        Country.ICELAND -> Res.string.iceland
        Country.INDIA -> Res.string.india
        Country.INDONESIA -> Res.string.indonesia
        Country.IRAN -> Res.string.iran
        Country.IRAQ -> Res.string.iraq
        Country.IRELAND -> Res.string.ireland
        Country.ISRAEL -> Res.string.israel
        Country.ITALY -> Res.string.italy
        Country.IVORY_COAST -> Res.string.ivory_coast
        Country.JAMAICA -> Res.string.jamaica
        Country.JAPAN -> Res.string.japan
        Country.JORDAN -> Res.string.jordan
        Country.KAZAKHSTAN -> Res.string.kazakhstan
        Country.KENYA -> Res.string.kenya
        Country.KIRIBATI -> Res.string.kiribati
        Country.KUWAIT -> Res.string.kuwait
        Country.KYRGYZSTAN -> Res.string.kyrgyzstan
        Country.LAOS -> Res.string.laos
        Country.LATVIA -> Res.string.latvia
        Country.LEBANON -> Res.string.lebanon
        Country.LESOTHO -> Res.string.lesotho
        Country.LIBERIA -> Res.string.liberia
        Country.LIBYA -> Res.string.libya
        Country.LIECHTENSTEIN -> Res.string.liechtenstein
        Country.LITHUANIA -> Res.string.lithuania
        Country.LUXEMBOURG -> Res.string.luxembourg
        Country.MADAGASCAR -> Res.string.madagascar
        Country.MALAWI -> Res.string.malawi
        Country.MALAYSIA -> Res.string.malaysia
        Country.MALDIVES -> Res.string.maldives
        Country.MALI -> Res.string.mali
        Country.MALTA -> Res.string.malta
        Country.MARSHALL_ISLANDS -> Res.string.marshall_islands
        Country.MAURITANIA -> Res.string.mauritania
        Country.MAURITIUS -> Res.string.mauritius
        Country.MEXICO -> Res.string.mexico
        Country.MICRONESIA -> Res.string.micronesia
        Country.MOLDOVA -> Res.string.moldova
        Country.MONACO -> Res.string.monaco
        Country.MONGOLIA -> Res.string.mongolia
        Country.MONTENEGRO -> Res.string.montenegro
        Country.MOROCCO -> Res.string.morocco
        Country.MOZAMBIQUE -> Res.string.mozambique
        Country.MYANMAR -> Res.string.myanmar
        Country.NAMIBIA -> Res.string.namibia
        Country.NAURU -> Res.string.nauru
        Country.NEPAL -> Res.string.nepal
        Country.NETHERLANDS -> Res.string.netherlands
        Country.NEW_ZEALAND -> Res.string.new_zealand
        Country.NICARAGUA -> Res.string.nicaragua
        Country.NIGER -> Res.string.niger
        Country.NIGERIA -> Res.string.nigeria
        Country.NORTH_KOREA -> Res.string.north_korea
        Country.NORTH_MACEDONIA -> Res.string.north_macedonia
        Country.NORWAY -> Res.string.norway
        Country.OMAN -> Res.string.oman
        Country.PAKISTAN -> Res.string.pakistan
        Country.PALAU -> Res.string.palau
        Country.PANAMA -> Res.string.panama
        Country.PAPUA_NEW_GUINEA -> Res.string.papua_new_guinea
        Country.PARAGUAY -> Res.string.paraguay
        Country.PERU -> Res.string.peru
        Country.PHILIPPINES -> Res.string.philippines
        Country.POLAND -> Res.string.poland*/
        Country.PORTUGAL -> Res.string.portugal
        /*Country.QATAR -> Res.string.qatar
        Country.ROMANIA -> Res.string.romania
        Country.RUSSIA -> Res.string.russia
        Country.RWANDA -> Res.string.rwanda
        Country.SAINT_KITTS_AND_NEVIS -> Res.string.saint_kitts_and_nevis
        Country.SAINT_LUCIA -> Res.string.saint_lucia
        Country.SAINT_VINCENT_AND_THE_GRENADINES -> Res.string.saint_vincent_and_the_grenadines
        Country.SAMOA -> Res.string.samoa
        Country.SAN_MARINO -> Res.string.san_marino
        Country.SAO_TOME_AND_PRINCIPE -> Res.string.sao_tome_and_principe
        Country.SAUDI_ARABIA -> Res.string.saudi_arabia
        Country.SENEGAL -> Res.string.senegal
        Country.SERBIA -> Res.string.serbia
        Country.SEYCHELLES -> Res.string.seychelles
        Country.SIERRA_LEONE -> Res.string.sierra_leone
        Country.SINGAPORE -> Res.string.singapore
        Country.SLOVAKIA -> Res.string.slovakia
        Country.SLOVENIA -> Res.string.slovenia
        Country.SOLOMON_ISLANDS -> Res.string.solomon_islands
        Country.SOMALIA -> Res.string.somalia
        Country.SOUTH_AFRICA -> Res.string.south_africa
        Country.SOUTH_KOREA -> Res.string.south_korea
        Country.SOUTH_SUDAN -> Res.string.south_sudan*/
        Country.SPAIN -> Res.string.spain
        /*Country.SRI_LANKA -> Res.string.sri_lanka
        Country.SUDAN -> Res.string.sudan
        Country.SURINAME -> Res.string.suriname
        Country.SWEDEN -> Res.string.sweden
        Country.SWITZERLAND -> Res.string.switzerland
        Country.SYRIA -> Res.string.syria
        Country.TAJIKISTAN -> Res.string.tajikistan
        Country.TANZANIA -> Res.string.tanzania
        Country.THAILAND -> Res.string.thailand
        Country.TIMOR_LESTE -> Res.string.timor_leste
        Country.TOGO -> Res.string.togo
        Country.TONGA -> Res.string.tonga
        Country.TRINIDAD_AND_TOBAGO -> Res.string.trinidad_and_tobago
        Country.TUNISIA -> Res.string.tunisia
        Country.TURKEY -> Res.string.turkey
        Country.TURKMENISTAN -> Res.string.turkmenistan
        Country.TUVALU -> Res.string.tuvalu
        Country.UGANDA -> Res.string.uganda
        Country.UKRAINE -> Res.string.ukraine
        Country.UNITED_ARAB_EMIRATES -> Res.string.united_arab_emirates*/
        Country.UNITED_KINGDOM -> Res.string.united_kingdom
        /*Country.UNITED_STATES -> Res.string.united_states
        Country.URUGUAY -> Res.string.uruguay
        Country.UZBEKISTAN -> Res.string.uzbekistan
        Country.VANUATU -> Res.string.vanuatu
        Country.VATICAN_CITY -> Res.string.vatican_city
        Country.VENEZUELA -> Res.string.venezuela
        Country.VIETNAM -> Res.string.vietnam
        Country.YEMEN -> Res.string.yemen
        Country.ZAMBIA -> Res.string.zambia
        Country.ZIMBABWE -> Res.string.zimbabwe*/
    }
}