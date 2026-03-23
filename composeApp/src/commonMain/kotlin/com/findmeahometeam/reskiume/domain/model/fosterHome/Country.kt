package com.findmeahometeam.reskiume.domain.model.fosterHome

import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.france
import reskiume.composeapp.generated.resources.portugal
import reskiume.composeapp.generated.resources.spain
import reskiume.composeapp.generated.resources.united_kingdom
import reskiume.composeapp.generated.resources.unselected_country

enum class Country() {
    UNSELECTED,
    /*AFGHANISTAN,
    ALBANIA,
    ALGERIA,
    ANDORRA,
    ANGOLA,
    ANTIGUA_AND_BARBUDA,
    ARGENTINA,
    ARMENIA,
    AUSTRALIA,
    AUSTRIA,
    AZERBAIJAN,
    BAHAMAS,
    BAHRAIN,
    BANGLADESH,
    BARBADOS,
    BELARUS,
    BELGIUM,
    BELIZE,
    BENIN,
    BHUTAN,
    BOLIVIA,
    BOSNIA_AND_HERZEGOVINA,
    BOTSWANA,
    BRAZIL,
    BRUNEI,
    BULGARIA,
    BURKINA_FASO,
    BURUNDI,
    CAMBODIA,
    CAMEROON,
    CANADA,
    CAPE_VERDE,
    CENTRAL_AFRICAN_REPUBLIC,
    CHAD,
    CHILE,
    CHINA,
    COLOMBIA,
    COMOROS,
    CONGO,
    COSTA_RICA,
    CROATIA,
    CUBA,
    CYPRUS,
    CZECH_REPUBLIC,
    DENMARK,
    DJIBOUTI,
    DOMINICA,
    DOMINICAN_REPUBLIC,
    ECUADOR,
    EGYPT,
    EL_SALVADOR,
    EQUATORIAL_GUINEA,
    ERITREA,
    ESTONIA,
    ESWATINI,
    ETHIOPIA,
    FIJI,
    FINLAND,*/
    FRANCE,
    /*GABON,
    GAMBIA,
    GEORGIA,
    GERMANY,
    GHANA,
    GREECE,
    GRENADA,
    GUATEMALA,
    GUINEA,
    GUINEA_BISSAU,
    GUYANA,
    HAITI,
    HONDURAS,
    HUNGARY,
    ICELAND,
    INDIA,
    INDONESIA,
    IRAN,
    IRAQ,
    IRELAND,
    ISRAEL,
    ITALY,
    IVORY_COAST,
    JAMAICA,
    JAPAN,
    JORDAN,
    KAZAKHSTAN,
    KENYA,
    KIRIBATI,
    KUWAIT,
    KYRGYZSTAN,
    LAOS,
    LATVIA,
    LEBANON,
    LESOTHO,
    LIBERIA,
    LIBYA,
    LIECHTENSTEIN,
    LITHUANIA,
    LUXEMBOURG,
    MADAGASCAR,
    MALAWI,
    MALAYSIA,
    MALDIVES,
    MALI,
    MALTA,
    MARSHALL_ISLANDS,
    MAURITANIA,
    MAURITIUS,
    MEXICO,
    MICRONESIA,
    MOLDOVA,
    MONACO,
    MONGOLIA,
    MONTENEGRO,
    MOROCCO,
    MOZAMBIQUE,
    MYANMAR,
    NAMIBIA,
    NAURU,
    NEPAL,
    NETHERLANDS,
    NEW_ZEALAND,
    NICARAGUA,
    NIGER,
    NIGERIA,
    NORTH_KOREA,
    NORTH_MACEDONIA,
    NORWAY,
    OMAN,
    PAKISTAN,
    PALAU,
    PANAMA,
    PAPUA_NEW_GUINEA,
    PARAGUAY,
    PERU,
    PHILIPPINES,
    POLAND,*/
    PORTUGAL,
    /*QATAR,
    ROMANIA,
    RUSSIA,
    RWANDA,
    SAINT_KITTS_AND_NEVIS,
    SAINT_LUCIA,
    SAINT_VINCENT_AND_THE_GRENADINES,
    SAMOA,
    SAN_MARINO,
    SAO_TOME_AND_PRINCIPE,
    SAUDI_ARABIA,
    SENEGAL,
    SERBIA,
    SEYCHELLES,
    SIERRA_LEONE,
    SINGAPORE,
    SLOVAKIA,
    SLOVENIA,
    SOLOMON_ISLANDS,
    SOMALIA,
    SOUTH_AFRICA,
    SOUTH_KOREA,
    SOUTH_SUDAN,*/
    SPAIN,
    /*SRI_LANKA,
    SUDAN,
    SURINAME,
    SWEDEN,
    SWITZERLAND,
    SYRIA,
    TAJIKISTAN,
    TANZANIA,
    THAILAND,
    TIMOR_LESTE,
    TOGO,
    TONGA,
    TRINIDAD_AND_TOBAGO,
    TUNISIA,
    TURKEY,
    TURKMENISTAN,
    TUVALU,
    UGANDA,
    UKRAINE,
    UNITED_ARAB_EMIRATES,*/
    UNITED_KINGDOM,
    /*UNITED_STATES,
    URUGUAY,
    UZBEKISTAN,
    VANUATU,
    VATICAN_CITY,
    VENEZUELA,
    VIETNAM,
    YEMEN,
    ZAMBIA,
    ZIMBABWE*/
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