public class X {

    Object o = (@Marker X) null;

    Object p = (@Marker X @Marker[]) null;

    Object q = (@Marker @Marker @Marker java.util.List<@Marker String>[]) null;

    Object r = (@Marker @Marker @Marker @Marker java.util.Map<@Marker String, @Marker String>.Entry<@Marker String, @Marker String> @Marker[]) null;
}
