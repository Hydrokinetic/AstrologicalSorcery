package swordstone.astrosorc.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import swordstone.astrosorc.AstrologicalSorcery;

public class ResourceProxy extends AbstractResourcePack {
	public static final String[] DEFAULT_RESOURCE_PACKS = new String[] { "aD", "field_110449_ao", "defaultResourcePacks" };

	private static final Set<String> RESOURCE_DOMAINS = ImmutableSet.of("astralsorcery", "minecraft");

	private static final String PACK_META = "pack.mcmeta";
	private static final String PROXYPACK_META = "/proxypack.mcmeta";

	private static final HashMap<String, Boolean> found = new HashMap<>();

	public static void init() {
		List<IResourcePack> resourcePacks = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), DEFAULT_RESOURCE_PACKS);
		IResourcePack resourceProxy = new ResourceProxy();
		resourcePacks.add(resourceProxy);

		AstrologicalSorcery.logger.info("Hooked Proxy Resource Pack");
	}

	private ResourceProxy() {
		super(Loader.instance().activeModContainer().getSource());
	}

	@Override
	public Set<String> getResourceDomains() {
		return RESOURCE_DOMAINS;
	}

	@Override
	protected InputStream getInputStreamByName(String name) throws IOException {
		if(name == null)
			return null;

		return stream(name);
	}

	@Override
	protected boolean hasResourceName(String name) {
		if(name.startsWith("assets/minecraft") && !name.contains("creative_inventory"))
			return false;

		if(!found.containsKey(name))
			locate(name);

		return found.get(name);
	}

	private void locate(String name) {
		InputStream stream = stream(name);
		found.put(name, stream != null);
	}

	private InputStream stream(String name) {
		if(name.equals(PACK_META))
			name = PROXYPACK_META;

		if(!name.startsWith("/"))
			name = "/" + name;
		
		if(name.contains(".lang")) {
			AstrologicalSorcery.logger.info("Intercepting request for original lang file at "+name);
			name = name.replaceAll("\\/assets\\/astralsorcery", "\\/assets\\/astrosorc");
			AstrologicalSorcery.logger.info("Redirecting to new lang file at "+name);
		}
		
		return AstrologicalSorcery.class.getResourceAsStream(name);
	}

	@Override
	public String getPackName() {
		return "astrosorc-lang-proxy";
	}

}
